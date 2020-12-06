package com.arthurzettler.giphygallery.ui.main.trending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun load() {
        GlobalScope.launch {
            val gifList = repository.getTrendingGifs()

            withContext(Dispatchers.Main) { internalGifList.value = gifList }
        }
    }
}