package id.rllyhz.schotersnewsapp.utils

import id.rllyhz.schotersnewsapp.BuildConfig
import id.rllyhz.schotersnewsapp.data.source.NewsRepository
import id.rllyhz.schotersnewsapp.data.source.remote.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object Constants {
    const val baseUrl = "https://newsapi.org/"
    const val apiKey = BuildConfig.API_KEY

    private fun getRemoteDataSource() = RemoteDataSource.getInstance()
    fun getRepository() = NewsRepository.getInstance(getRemoteDataSource())

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