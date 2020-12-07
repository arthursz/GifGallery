package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.Gif

interface GifRepository {
    suspend fun getTrendingGifs() : Result<List<Gif>>
    suspend fun getGifsForSearchQuery(query: String) : Result<List<Gif>>
    suspend fun getFavoriteGifs() : Result<List<Gif>>
    suspend fun storeFavoriteGif(gif: Gif)
    suspend fun removeFavoriteGif(gifId: String)
}