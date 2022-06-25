package id.rllyhz.schotersnewsapp.ui.features.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.databinding.FragmentSearchBinding
import id.rllyhz.schotersnewsapp.ui.adapters.ArticleListAdapter
import id.rllyhz.schotersnewsapp.utils.Constants
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show

class SearchFragment : Fragment(), ArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var articleListAdapter: ArticleListAdapter? = null
    private var editorListener: TextView.OnEditorActionListener? = null
    private var viewModel: SearchViewModel? = null

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
            SearchViewModel.Factory(Constants.getRepository(), Constants.dispatchersProvider)
        )[SearchViewModel::class.java]

        createEditorListener()

        viewModel?.let { vm ->
            binding.apply {
                searchEtSearch.setOnEditorActionListener(editorListener)
                searchLoadingAnimView.hide()

                searchRv.adapter = articleListAdapter
                searchRv.layoutManager = LinearLayoutManager(requireContext())
                searchRv.hide()
            }

            vm.searchNews.observe(viewLifecycleOwner) { news ->
                if (!news.isNullOrEmpty()) {
                    binding.apply {
                        articleListAdapter?.submitList(news)

                        searchRv.show()
                    }
                } else {
                    // data not found
                }

                binding.apply {
                    searchLoadingAnimView.hide()
                    searchLoadingAnimView.cancelAnimation()
                }
            }

            vm.shouldLoading.observe(viewLifecycleOwner) { loading ->
                if (loading) {
                    binding.apply {
                        searchRv.hide()
                        searchLoadingAnimView.show()
                        searchLoadingAnimView.playAnimation()
                    }
                }
            }

            vm.isError.observe(viewLifecycleOwner) { errorOccurred ->
                if (errorOccurred) {
                    binding.apply {
                        searchRv.hide()
                        searchLoadingAnimView.hide()
                        searchLoadingAnimView.cancelAnimation()
                    }
                }
            }
        }
    }

    private fun searchNews() {
        val query = binding.searchEtSearch.text.toString()
        viewModel?.searchNews(query)
    }

    private fun createEditorListener() {
        editorListener = TextView.OnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> searchNews()
            }
            true
        }
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