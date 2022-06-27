package id.rllyhz.schotersnewsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.rllyhz.schotersnewsapp.R
import id.rllyhz.schotersnewsapp.data.models.FavArticle
import id.rllyhz.schotersnewsapp.databinding.ItemArticleBinding
import id.rllyhz.schotersnewsapp.utils.formalizeDate

class FavArticleListAdapter :
    ListAdapter<FavArticle, FavArticleListAdapter.FavArticleListViewHolder>(
        FavArticleComparator()
    ) {
    private var callback: ItemClickCallback? = null

    fun setOnItemClickListener(listener: ItemClickCallback) {
        callback = listener
    }

    // removing listener to avoid memory leaks
    fun removeItemClickListener() {
        callback = null
    }

    // access to getting the single fav news
    fun getFavNews(position: Int): FavArticle =
        getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavArticleListViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavArticleListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavArticleListViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null)
            holder.bind(currentItem)
    }

    inner class FavArticleListViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favArticle: FavArticle) {
            binding.apply {

                Glide.with(itemView)
                    .asBitmap()
                    .centerCrop()
                    .load(favArticle.urlToImage)
                    .placeholder(R.drawable.bg_placeholder_article_cover)
                    .into(itemArticleCover)

                itemArticleTitle.text = favArticle.title
                itemArticlePublishedAt.text = formalizeDate(favArticle.publishedAt)

                itemView.setOnClickListener {
                    callback?.onClick(favArticle)
                }
            }
        }
    }

    // callback for handling itemClick event
    interface ItemClickCallback {
        fun onClick(favArticle: FavArticle)
    }

    // Diffutil for comparison
    class FavArticleComparator : DiffUtil.ItemCallback<FavArticle>() {
        override fun areItemsTheSame(oldItem: FavArticle, newItem: FavArticle): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FavArticle, newItem: FavArticle): Boolean =
            oldItem == newItem
    }
}