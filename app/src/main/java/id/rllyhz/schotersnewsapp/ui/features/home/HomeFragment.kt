package id.rllyhz.schotersnewsapp.ui.features.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.databinding.FragmentHomeBinding
import id.rllyhz.schotersnewsapp.ui.adapters.ArticleListAdapter
import id.rllyhz.schotersnewsapp.utils.Constants
import id.rllyhz.schotersnewsapp.utils.Resource
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), ArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var articleListAdapter: ArticleListAdapter? = null
    private var viewModel: HomeViewModel? = null

    // this should be injected by di
    private val dispatchers = Constants.dispatchersProvider
    private val repository = Constants.getRepository()

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory(repository)
        )[HomeViewModel::class.java]

        binding.apply {
            homeRv.adapter = articleListAdapter
            homeRv.layoutManager = LinearLayoutManager(requireContext())
            homeErrorBtnTryAgain.setOnClickListener { loadData() }
        }

        loadData()
    }

    private fun loadData() {
        // set loading UI at first
        setLoadingUI()

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch(Constants.dispatchersProvider.io) {
                viewModel?.let {
                    it.getNews().asFlow().distinctUntilChanged().collect { dataResources ->
                        when (dataResources) {
                            is Resource.Loading -> withContext(dispatchers.main) { setLoadingUI() }
                            is Resource.Error -> withContext(dispatchers.main) { setErrorUI() }
                            is Resource.Success -> {
                                val news = dataResources.data

                                if (!news.isNullOrEmpty()) {
                                    withContext(dispatchers.main) { setSuccessUI(news) }
                                } else {
                                    // data not found
                                    withContext(dispatchers.main) { setErrorUI() }
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun setInitialUI() {
        binding.apply {
            homeShimmerLayout.hide()
            if (homeShimmerLayout.isShimmerStarted) {
                homeShimmerLayout.stopShimmer()
            }
            homeErrorView.hide()
            homeRv.hide()
        }
    }

    private fun setLoadingUI() {
        setInitialUI()

        binding.apply {
            homeShimmerLayout.show()
            if (!homeShimmerLayout.isShimmerStarted)
                homeShimmerLayout.startShimmer()
        }
    }

    private fun setSuccessUI(data: List<Article>?) {
        setInitialUI()

        articleListAdapter?.submitList(data)
        binding.homeRv.show()
    }

    private fun setErrorUI() {
        setInitialUI()
        binding.homeErrorView.show()
    }

    override fun onClick(article: Article) {
        Log.d("myapp", "Item clicked!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.homeRv.adapter = null
        _binding = null
        viewModel = null
    }

    override fun onDestroy() {
        super.onDestroy()
        articleListAdapter?.removeItemClickListener()
        articleListAdapter = null
    }
}