package com.arthurzettler.gifgallery.data.source

import com.arthurzettler.gifgallery.data.Gif

interface GifDataSource {
    suspend fun getTrendingGifs(): List<Gif>
    suspend fun getGifsForSearchQuery(query: String): List<Gif>
    suspend fun getFavoriteGifs(): List<Gif>
    suspend fun storeGif(gif: Gif)
    suspend fun removeGif(gifId: String)
}
