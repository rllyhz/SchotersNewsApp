package id.rllyhz.schotersnewsapp.ui.features.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import id.rllyhz.schotersnewsapp.ui.detail.DetailActivity
import id.rllyhz.schotersnewsapp.ui.main.MainActivity
import id.rllyhz.schotersnewsapp.utils.Resource
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), ArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var articleListAdapter: ArticleListAdapter? = null
    private var editorListener: TextView.OnEditorActionListener? = null
    private var viewModel: SearchViewModel? = null

    private var _activity: MainActivity? = null

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

        _activity?.let { activity ->
            viewModel = ViewModelProvider(
                this,
                SearchViewModel.Factory(activity.repository)
            )[SearchViewModel::class.java]

            createEditorListener()

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

        _activity?.let { activity ->
            binding.apply {
                val query = binding.searchEtSearch.text.toString()

                viewLifecycleOwner.lifecycleScope.launch(activity.dispatchers.io) {
                    viewModel?.let {
                        it.searchNews(query).asFlow().distinctUntilChanged()
                            .collect { searchResult ->
                                when (searchResult) {
                                    is Resource.Loading -> withContext(activity.dispatchers.main) { setLoadingUI() }
                                    is Resource.Error -> withContext(activity.dispatchers.main) { setErrorUI() }
                                    is Resource.Success -> {
                                        val news = searchResult.data

                                        if (!news.isNullOrEmpty()) {
                                            withContext(activity.dispatchers.main) {
                                                setSuccessUI(
                                                    news
                                                )
                                            }
                                        } else {
                                            // data not found
                                            withContext(activity.dispatchers.main) { setDataNotFoundUI() }
                                        }
                                    }
                                    else -> Unit
                                }
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
        Intent(requireContext().applicationContext, DetailActivity::class.java).run {
            putExtra(DetailActivity.ARTICLE_KEY, article)
            startActivity(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _activity = context as MainActivity
    }

    override fun onDetach() {
        super.onDetach()
        _activity = null
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