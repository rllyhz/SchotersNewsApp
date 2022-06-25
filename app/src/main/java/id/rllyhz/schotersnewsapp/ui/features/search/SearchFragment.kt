package id.rllyhz.schotersnewsapp.ui.features.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.databinding.FragmentSearchBinding
import id.rllyhz.schotersnewsapp.ui.adapters.ArticleListAdapter
import id.rllyhz.schotersnewsapp.utils.Constants
import id.rllyhz.schotersnewsapp.utils.Resource
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), ArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var articleListAdapter: ArticleListAdapter? = null
    private var editorListener: TextView.OnEditorActionListener? = null
    private var viewModel: SearchViewModel? = null

    // this should be injected by DI
    private val repository = Constants.getRepository()
    private val dispatchers = Constants.dispatchersProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        articleListAdapter = ArticleListAdapter()
        articleListAdapter?.setOnItemClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.Factory(repository)
        )[SearchViewModel::class.java]

        createEditorListener()

        viewModel?.let { vm ->
            binding.apply {
                searchEtSearch.setOnEditorActionListener(editorListener)
                searchRv.adapter = articleListAdapter
                searchRv.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun searchNews() {
        // set loading UI at first
        setLoadingUI()

        binding.apply {
            val query = binding.searchEtSearch.text.toString()

            viewLifecycleOwner.lifecycleScope.launch(dispatchers.io) {
                viewModel?.let {
                    it.searchNews(query).asFlow().distinctUntilChanged().collect { searchResult ->
                        when (searchResult) {
                            is Resource.Loading -> withContext(dispatchers.main) { setLoadingUI() }
                            is Resource.Error -> withContext(dispatchers.main) { setErrorUI() }
                            is Resource.Success -> {
                                val news = searchResult.data

                                if (!news.isNullOrEmpty()) {
                                    withContext(dispatchers.main) { setSuccessUI(news) }
                                } else {
                                    // data not found
                                    withContext(dispatchers.main) { setDataNotFoundUI() }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun createEditorListener() {
        editorListener = TextView.OnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> searchNews()
            }
            true
        }
    }

    private fun setInitialUI() {
        binding.apply {
            searchRv.hide()
            searchErrorTextView.hide()
            searchDataNotFoundTextView.hide()
            searchLoadingAnimView.hide()
            if (searchLoadingAnimView.isAnimating)
                searchLoadingAnimView.cancelAnimation()
        }
    }

    private fun setLoadingUI() {
        setInitialUI()

        binding.apply {
            searchLoadingAnimView.show()
            if (!searchLoadingAnimView.isAnimating)
                searchLoadingAnimView.playAnimation()
        }
    }

    private fun setSuccessUI(data: List<Article>?) {
        setInitialUI()

        articleListAdapter?.submitList(data)
        binding.searchRv.show()
    }

    private fun setDataNotFoundUI() {
        setInitialUI()
        binding.searchDataNotFoundTextView.show()
    }

    private fun setErrorUI() {
        setInitialUI()
        binding.searchErrorTextView.show()
    }

    override fun onClick(article: Article) {
        Log.d("myapp", "Searched Item clicked!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchEtSearch.setOnEditorActionListener(null)
        binding.searchRv.adapter = null
        editorListener = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        articleListAdapter?.removeItemClickListener()
        articleListAdapter = null
    }
}