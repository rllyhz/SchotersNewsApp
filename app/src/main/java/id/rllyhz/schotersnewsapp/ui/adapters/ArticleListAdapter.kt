package id.rllyhz.schotersnewsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.rllyhz.schotersnewsapp.R
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.databinding.ItemArticleBinding
import id.rllyhz.schotersnewsapp.utils.formalizeDate

class ArticleListAdapter :
    ListAdapter<Article, ArticleListAdapter.ArticleListViewHolder>(ArticleComparator()) {
    private var callback: ItemClickCallback? = null

    fun setOnItemClickListener(listener: ItemClickCallback) {
        callback = listener
    }

    // removing listener to avoid memory leaks
    fun removeItemClickListener() {
        callback = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleListViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleListViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null)
            holder.bind(currentItem)
    }

    inner class ArticleListViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {

                Glide.with(itemView)
                    .asBitmap()
                    .centerCrop()
                    .load(article.urlToImage)
                    .placeholder(R.drawable.bg_placeholder_article_cover)
                    .into(itemArticleCover)

                itemArticleTitle.text = article.title
                itemArticlePublishedAt.text = formalizeDate(article.publishedAt)

                itemView.setOnClickListener { callback?.onClick(article) }
            }
        }
    }

    // callback for handling itemClick event
    interface ItemClickCallback {
        fun onClick(article: Article)
    }

    // Diffutil for comparison
    class ArticleComparator : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
            (oldItem.title == newItem.title) && (oldItem.url == newItem.url) && (oldItem.publishedAt == newItem.publishedAt)
                    && (oldItem.urlToImage == newItem.urlToImage)

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
            oldItem == newItem
    }
}