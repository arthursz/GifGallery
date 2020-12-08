package com.arthurzettler.gifgallery.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurzettler.gifgallery.data.Gif
import com.arthurzettler.gifgallery.data.Result
import com.arthurzettler.gifgallery.data.source.GifRepository
import com.arthurzettler.gifgallery.data.source.GifRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GifViewModel(private val repository: GifRepository = GifRepositoryImpl()) : ViewModel() {

    private val internalGifList = MutableLiveData<MutableList<Gif>>(mutableListOf())
    val gifList: LiveData<MutableList<Gif>> = internalGifList

    private val internalFavoriteGifList = MutableLiveData<MutableList<Gif>>(mutableListOf())
    val favoriteGifList: LiveData<MutableList<Gif>> = internalFavoriteGifList

    private val internalHasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = internalHasError

    private var currentPage = 0

    fun load() = viewModelScope.launch {
        reset()

        if (loadFavoriteGifs().not()) return@launch

        when (val result = repository.getTrendingGifs(currentPage)) {
            is Result.Success<List<Gif>> -> internalGifList.setValueOnUiThread(result.value.setFavorites().toMutableList())
            is Result.Failure -> internalHasError.setValueOnUiThread(true)
        }
    }

    fun search(query: String) = viewModelScope.launch {
        reset()

        when (val result = repository.getGifsForSearchQuery(query, currentPage)) {
            is Result.Success<List<Gif>> -> internalGifList.setValueOnUiThread(result.value.setFavorites().toMutableList())
            is Result.Failure -> internalHasError.setValueOnUiThread(true)
        }
    }

    fun paginate(query: String) = viewModelScope.launch {
        currentPage++

        val page = currentPage * 25
        val result = if (query.isEmpty()) repository.getTrendingGifs(page)
                     else repository.getGifsForSearchQuery(query, page)

        if (result is Result.Success<List<Gif>>) {
            internalGifList.value?.let {
                it.addAll(result.value.setFavorites())
                internalGifList.setValueOnUiThread(it)
            }
        }
    }

    fun setFavoriteGif(gif: Gif, isFavorite: Boolean) = viewModelScope.launch {
        gif.isFavorited = isFavorite

        when (isFavorite) {
            true -> addFavoriteGif(gif)
            false -> removeFavoriteGif(gif)
        }

        internalGifList.value?.setFavorite(gif, isFavorite)
    }

    fun notifyFavoriteGifListObserver() { internalFavoriteGifList.notifyObserver() }

    fun notifyGifListObserver() { internalGifList.notifyObserver() }

    private suspend fun reset() {
        currentPage = 0
        internalHasError.setValueOnUiThread(false)
    }

    private suspend fun loadFavoriteGifs() =
        when (val result = repository.getFavoriteGifs()) {
            is Result.Success<List<Gif>> -> {
                internalFavoriteGifList.setValueOnUiThread(result.value.toMutableList())
                true
            }
            is Result.Failure -> {
                internalHasError.setValueOnUiThread(true)
                false
            }
        }

    private suspend fun removeFavoriteGif(gif: Gif) {
        internalFavoriteGifList.value?.remove(gif)
        repository.removeFavoriteGif(gif.id)
    }

    private suspend fun addFavoriteGif(gif: Gif) {
        internalFavoriteGifList.value?.add(gif)
        repository.storeFavoriteGif(gif)
    }

    private fun List<Gif>.setFavorites() : List<Gif> {
        internalFavoriteGifList.value?.forEach { setFavorite(it, it.isFavorited) }
        return this
    }

    private fun List<Gif>.setFavorite(gif: Gif, isFavorite: Boolean) {
        this.find { it.id == gif.id }?.isFavorited = isFavorite
    }

    private suspend fun <T> MutableLiveData<T>.setValueOnUiThread(newValue: T) =
        withContext(Dispatchers.Main) { value = newValue }
    private fun <T> MutableLiveData<T>.notifyObserver() { this.value = this.value }
}