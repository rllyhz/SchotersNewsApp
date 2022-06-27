package id.rllyhz.schotersnewsapp.ui.features.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.databinding.FragmentFavoritesBinding
import id.rllyhz.schotersnewsapp.ui.adapters.FavArticleListAdapter
import id.rllyhz.schotersnewsapp.ui.detail.DetailActivity
import id.rllyhz.schotersnewsapp.utils.asModel
import id.rllyhz.schotersnewsapp.utils.hide

class FavoritesFragment : Fragment(), FavArticleListAdapter.ItemClickCallback {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private var favArticleListAdapter: FavArticleListAdapter? = null

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

        binding.apply {
            favoritesRv.layoutManager = LinearLayoutManager(requireContext())
            favoritesRv.adapter = null
            favoritesRv.hide()
            favoritesEmptyText.hide()
        }
    }

    override fun onClick(favArticle: FavArticle) {
        Intent(requireContext().applicationContext, DetailActivity::class.java).run {
            putExtra(DetailActivity.ARTICLE_KEY, favArticle.asModel())
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        favArticleListAdapter?.removeItemClickListener()
        favArticleListAdapter = null
    }
}