package id.rllyhz.schotersnewsapp.ui.features.favorites

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import id.rllyhz.schotersnewsapp.R
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
    private var _activityResultLaunch: ActivityResultLauncher<Intent>? = null

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
                favoritesRv.adapter = favArticleListAdapter

                val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean = true

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition

                        favArticleListAdapter?.let {
                            val favNews = it.getFavNews(position)
                            _viewModel?.deleteFavNews(favNews)

                            Snackbar.make(
                                binding.favoritesCoordinatorLayout,
                                getString(R.string.detail_news_deleted_message),
                                Snackbar.LENGTH_SHORT
                            )
                                .setAction(getString(R.string.detail_btn_oke_text)) {
                                    //
                                }
                                .show()
                        }
                    }
                }

                ItemTouchHelper(itemTouchHelperCallback)
                    .attachToRecyclerView(binding.favoritesRv)

                setInitialUI()

                loadFavNews()
            }
        }
    }

    private fun loadFavNews() {
        _viewModel?.let { viewModel ->
            viewModel.getAllFavNews().observe(viewLifecycleOwner) {
                if (it.isNullOrEmpty()) showNoDataUI() else showHasDataUI(it)
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
        favArticleListAdapter?.notifyDataSetChanged()
        binding.favoritesRv.show()
    }

    private fun showNoDataUI() {
        setInitialUI()
        binding.favoritesEmptyText.show()
    }

    override fun onClick(favArticle: FavArticle) {
        Intent(requireContext().applicationContext, DetailActivity::class.java).run {
            putExtra(DetailActivity.ARTICLE_KEY, favArticle.asModel())
            _activityResultLaunch?.launch(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _activity = context as MainActivity

        _activityResultLaunch = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == DetailActivity.DETAIL_RESULT_CODE) {
                // re-load data from db
                // somehow typed Flow of getAllNews() method on Dao not
                // updating the newest emit data directly when deleting in detail activity,
                // so have to trigger it manually by re-retrieving and re-observing the data
                loadFavNews()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        _activityResultLaunch = null
        _activity = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.favoritesRv.adapter = null
        _binding = null
        _viewModel = null
    }

    override fun onDestroy() {
        super.onDestroy()
        favArticleListAdapter?.removeItemClickListener()
        favArticleListAdapter = null
    }
}