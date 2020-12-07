package com.arthurzettler.giphygallery.data.source.local

import com.arthurzettler.giphygallery.ContextHolder
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.data.source.GifDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GifLocalDataSource(
    private val database: GifDatabase = GifDatabase.getDatabase(ContextHolder.context)
) : GifDataSource {
    override suspend fun getTrendingGifs() = listOf<Gif>()
    override suspend fun getGifsForSearchQuery(query: String) = listOf<Gif>()

    override suspend fun getFavoriteGifs() = withContext(Dispatchers.IO) {
        database.gifDao().getGifs()
    }

    override suspend fun storeGif(gif: Gif) = withContext(Dispatchers.IO) {
        database.gifDao().insertGif(gif)
    }

    override suspend fun removeGif(gifId: String) = withContext(Dispatchers.IO) {
        database.gifDao().removeGif(gifId)
    }
}