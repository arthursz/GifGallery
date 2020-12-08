package com.arthurzettler.gifgallery.data.source

import com.arthurzettler.gifgallery.data.Gif

interface GifDataSource {
    suspend fun getTrendingGifs(page: Int): List<Gif>
    suspend fun getGifsForSearchQuery(query: String, page: Int): List<Gif>
    suspend fun getFavoriteGifs(): List<Gif>
    suspend fun storeGif(gif: Gif)
    suspend fun removeGif(gifId: String)
}
