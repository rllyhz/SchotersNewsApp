package id.rllyhz.schotersnewsapp.ui.features.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.databinding.FragmentHomeBinding
import id.rllyhz.schotersnewsapp.ui.adapters.ArticleListAdapter
import id.rllyhz.schotersnewsapp.ui.detail.DetailActivity
import id.rllyhz.schotersnewsapp.ui.main.MainActivity
import id.rllyhz.schotersnewsapp.utils.Resource
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), ArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var articleListAdapter: ArticleListAdapter? = null
    private var viewModel: HomeViewModel? = null

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _activity?.let { activity ->
            viewModel = ViewModelProvider(
                this,
                HomeViewModel.Factory(activity.repository)
            )[HomeViewModel::class.java]

            binding.apply {
                homeRv.adapter = articleListAdapter
                homeRv.layoutManager = LinearLayoutManager(requireContext())
                homeErrorBtnTryAgain.setOnClickListener { loadData() }
            }

            loadData()
        }
    }

    private fun loadData() {
        // set loading UI at first
        setLoadingUI()

        _activity?.let { activity ->
            binding.apply {
                viewLifecycleOwner.lifecycleScope.launch(activity.dispatchers.io) {
                    viewModel?.let {
                        it.getNews().asFlow().distinctUntilChanged().collect { dataResources ->
                            when (dataResources) {
                                is Resource.Loading -> withContext(activity.dispatchers.main) { setLoadingUI() }
                                is Resource.Error -> withContext(activity.dispatchers.main) { setErrorUI() }
                                is Resource.Success -> {
                                    val news = dataResources.data

                                    if (!news.isNullOrEmpty()) {
                                        withContext(activity.dispatchers.main) { setSuccessUI(news) }
                                    } else {
                                        // data not found
                                        withContext(activity.dispatchers.main) { setErrorUI() }
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