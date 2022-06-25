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

class HomeFragment : Fragment(), ArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var articleListAdapter: ArticleListAdapter? = null
    private var viewModel: HomeViewModel? = null

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
            HomeViewModel.Factory(Constants.getRepository())
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
            lifecycleScope.launchWhenResumed {
                viewModel?.getNews()?.observe(viewLifecycleOwner) { resources ->
                    when (resources) {
                        is Resource.Loading -> setLoadingUI()
                        is Resource.Error -> setErrorUI()
                        is Resource.Success -> {
                            val news = resources.data

                            if (!news.isNullOrEmpty()) {
                                setSuccessUI(news)
                            } else {
                                // data not found
                                setErrorUI()
                            }
                        }
                        else -> Unit
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