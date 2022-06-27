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
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.databinding.ActivityDetailBinding
import id.rllyhz.schotersnewsapp.utils.Constants
import id.rllyhz.schotersnewsapp.utils.DispatcherProvider
import id.rllyhz.schotersnewsapp.utils.formalizeDate

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    // these should be injected by DI
    private lateinit var repository: NewsRepository
    private lateinit var dispatcherProvider: DispatcherProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        repository = Constants.getRepository(this)
        dispatcherProvider = Constants.dispatchersProvider
        val news = intent.getSerializableExtra(ARTICLE_KEY) as Article

        viewModel = ViewModelProvider(
            this,
            DetailViewModel.Factory(repository, dispatcherProvider, news)
        )[DetailViewModel::class.java]

        binding.apply {
            detailToolbar.run {
                setNavigationIcon(R.drawable.ic_arrow_back)

                setNavigationOnClickListener {
                    finishWithResult()
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
                viewModel.addOrDeleteFromFav(
                    news, listOf(
                        getString(R.string.detail_news_added_message),
                        getString(R.string.detail_news_failed_to_add_message),
                        getString(R.string.detail_news_deleted_message),
                        getString(R.string.detail_news_failed_to_delete_message)
                    )
                )
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

            viewModel.isFavorite.observe(this@DetailActivity) { fav ->
                detailFavoriteFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        if (fav) R.drawable.ic_fav_filled else R.drawable.ic_fav_outlined
                    )
                )
            }

            viewModel.showMessageEvent.observe(this@DetailActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    Snackbar.make(detailCoordinatorLayout, message, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.detail_btn_oke_text)) {
                            //
                        }
                        .show()
                }
            }
        }
    }

    override fun onBackPressed() {
        finishWithResult()
    }

    private fun finishWithResult() {
        setResult(DETAIL_RESULT_CODE)
        finish()
    }

    companion object {
        const val ARTICLE_KEY = "ARTICLE_KEY"
        const val DETAIL_RESULT_CODE = 123
    }
}