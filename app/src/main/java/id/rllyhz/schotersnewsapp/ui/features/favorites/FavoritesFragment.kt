package id.rllyhz.schotersnewsapp.ui.features.favorites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.databinding.FragmentFavoritesBinding
import id.rllyhz.schotersnewsapp.ui.adapters.FavArticleListAdapter
import id.rllyhz.schotersnewsapp.ui.detail.DetailActivity
import id.rllyhz.schotersnewsapp.ui.main.MainActivity
import id.rllyhz.schotersnewsapp.utils.asModel
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show

class FavoritesFragment : Fragment(), FavArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private var favArticleListAdapter: FavArticleListAdapter? = null
    private var _viewModel: FavoritesViewModel? = null
    private var _activity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favArticleListAdapter = FavArticleListAdapter()
        favArticleListAdapter?.setOnItemClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _activity?.let { activity ->
            _viewModel = ViewModelProvider(
                this,
                FavoritesViewModel.Factory(activity.repository, activity.dispatchers)
            )[FavoritesViewModel::class.java]

            binding.apply {
                favoritesRv.layoutManager = LinearLayoutManager(requireContext())
                favoritesRv.adapter = null
                setInitialUI()

                _viewModel?.let { viewModel ->
                    viewModel.getAllNews().observe(viewLifecycleOwner) {
                        if (it.isNullOrEmpty()) showNoDataUI() else showHasDataUI(it)
                    }
                }
            }
        }
    }

    private fun setInitialUI() {
        binding.apply {
            favoritesRv.hide()
            favoritesEmptyText.hide()
        }
    }

    private fun showHasDataUI(data: List<FavArticle>) {
        setInitialUI()

        favArticleListAdapter?.submitList(data)
        binding.favoritesRv.show()
    }

    private fun showNoDataUI() {
        setInitialUI()
        binding.favoritesEmptyText.show()
    }

    override fun onClick(favArticle: FavArticle) {
        Intent(requireContext().applicationContext, DetailActivity::class.java).run {
            putExtra(DetailActivity.ARTICLE_KEY, favArticle.asModel())
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
        _binding = null
        _viewModel = null
    }

    override fun onDestroy() {
        super.onDestroy()
        favArticleListAdapter?.removeItemClickListener()
        favArticleListAdapter = null
    }
}