package id.rllyhz.schotersnewsapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import id.rllyhz.schotersnewsapp.R
import id.rllyhz.schotersnewsapp.data.models.Article
import id.rllyhz.schotersnewsapp.databinding.ActivityDetailBinding
import id.rllyhz.schotersnewsapp.utils.Constants
import id.rllyhz.schotersnewsapp.utils.formalizeDate

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    private val repository = Constants.getRepository()

    private var fav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(
            this,
            DetailViewModel.Factory(repository)
        )[DetailViewModel::class.java]

        val news = intent.getSerializableExtra(ARTICLE_KEY) as Article

        binding.apply {
            detailToolbar.run {
                setNavigationIcon(R.drawable.ic_arrow_back)

                setNavigationOnClickListener {
                    finish()
                }
            }

            val contentOrDesc = news.content ?: news.description

            detailNewsTitle.text = news.title
            detailNewsPublishedAt.text = formalizeDate(news.publishedAt)
            detailNewsAuthors.text = news.author ?: "-"
            detailNewsContent.text = contentOrDesc

            Glide.with(this@DetailActivity)
                .load(news.urlToImage)
                .centerCrop()
                .placeholder(R.drawable.bg_placeholder_article_cover)
                .into(detailNewsCover)

            detailFavoriteFab.setOnClickListener {
                viewModel.addOrDeleteFromFav()
                val message =
                    if (fav) getString(R.string.detail_news_added_message) else getString(R.string.detail_news_deleted_message)

                Snackbar.make(
                    detailCoordinatorLayout,
                    message,
                    Snackbar.LENGTH_SHORT
                )
                    .setAction(getString(R.string.detail_btn_oke_text)) {
                        //
                    }
                    .show()
            }

            // set see more link underlined
            val spannableString = SpannableString(getString(R.string.detail_see_more_label))
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            detailNewsSeeMoreLink.text = spannableString

            detailNewsSeeMoreLink.setOnClickListener {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(news.url)
                    startActivity(this)
                }
            }

            viewModel.isFavorite.observe(this@DetailActivity) { favorite ->
                fav = favorite

                detailFavoriteFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        if (fav) R.drawable.ic_fav_filled else R.drawable.ic_fav_outlined
                    )
                )
            }
        }
    }

    companion object {
        const val ARTICLE_KEY = "ARTICLE_KEY"
    }
}