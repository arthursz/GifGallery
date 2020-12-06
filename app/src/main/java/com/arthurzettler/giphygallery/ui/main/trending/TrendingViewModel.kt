package com.arthurzettler.giphygallery.ui.main.trending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.data.source.GifRepository
import com.arthurzettler.giphygallery.data.source.GifRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendingViewModel(private val repository: GifRepository = GifRepositoryImpl()) : ViewModel() {
    private val internalGifList = MutableLiveData<List<Gif>>()
    val giftList: LiveData<List<Gif>> = internalGifList

    private val internalHasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = internalHasError

    fun load() = GlobalScope.launch {
        internalHasError.setValueOnUiThread(false)

        when (val result = repository.getTrendingGifs()) {
            is Result.Success<List<Gif>> -> internalGifList.setValueOnUiThread(result.value)
            is Result.Failure -> internalHasError.setValueOnUiThread(true)
        }
    }

    private suspend fun <T> MutableLiveData<T>.setValueOnUiThread(newValue: T) =
        withContext(Dispatchers.Main) { value = newValue }
}