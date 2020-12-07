package com.arthurzettler.gifgallery.data.source

import com.arthurzettler.gifgallery.data.Result
import com.arthurzettler.gifgallery.data.Gif

interface GifRepository {
    suspend fun getTrendingGifs() : Result<List<Gif>>
    suspend fun getGifsForSearchQuery(query: String) : Result<List<Gif>>
    suspend fun getFavoriteGifs() : Result<List<Gif>>
    suspend fun storeFavoriteGif(gif: Gif)
    suspend fun removeFavoriteGif(gifId: String)
}