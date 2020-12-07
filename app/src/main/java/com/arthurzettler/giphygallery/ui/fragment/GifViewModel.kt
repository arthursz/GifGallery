package com.arthurzettler.giphygallery.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.data.source.GifRepository
import com.arthurzettler.giphygallery.data.source.GifRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GifViewModel(private val repository: GifRepository = GifRepositoryImpl()) : ViewModel() {
    private val internalGifList = MutableLiveData<List<Gif>>(listOf())
    val gifList: LiveData<List<Gif>> = internalGifList

    private val internalFavoriteGifList = MutableLiveData<MutableList<Gif>>(mutableListOf())
    val favoriteGifList: LiveData<MutableList<Gif>> = internalFavoriteGifList

    private val internalHasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = internalHasError

    fun load() = viewModelScope.launch {
        internalHasError.setValueOnUiThread(false)

        when (val result = repository.getTrendingGifs()) {
            is Result.Success<List<Gif>> -> {
                internalGifList.setValueOnUiThread(result.value)
            }
            is Result.Failure -> internalHasError.setValueOnUiThread(true)
        }
    }

    fun setFavoriteGif(gif: Gif, isFavorite: Boolean) {
        gif.isFavorited = isFavorite

        when (isFavorite) {
            true -> internalFavoriteGifList.value?.add(gif)
            false -> internalFavoriteGifList.value?.remove(gif)
        }

        internalGifList.value?.find { it.id == gif.id }?.isFavorited = isFavorite
    }

    fun notifyFavoriteGifListObserver() { internalFavoriteGifList.notifyObserver() }

    fun notifyGifListObserver() { internalGifList.notifyObserver() }

    private suspend fun <T> MutableLiveData<T>.setValueOnUiThread(newValue: T) =
        withContext(Dispatchers.Main) { value = newValue }

    private fun <T> MutableLiveData<T>.notifyObserver() { this.value = this.value }
}