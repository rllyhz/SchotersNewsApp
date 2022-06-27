package id.rllyhz.schotersnewsapp.utils

import android.content.Context
import id.rllyhz.schotersnewsapp.BuildConfig
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.data.source.local.LocalDataSource
import id.rllyhz.schotersnewsapp.data.source.remote.RemoteDataSource
import id.rllyhz.schotersnewsapp.db.NewsDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Constants {
    const val baseUrl = "https://newsapi.org/"
    const val apiKey = BuildConfig.API_KEY

    private fun getDao(context: Context) = NewsDatabase.getInstance(context).getDao()
    private fun getRemoteDataSource() = RemoteDataSource.getInstance()
    private fun getLocalDataSource(context: Context) = LocalDataSource.getInstance(getDao(context))

    fun getRepository(context: Context) =
        NewsRepository.getInstance(
            getRemoteDataSource(),
            getLocalDataSource(context),
            dispatchersProvider
        )

    val dispatchersProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}