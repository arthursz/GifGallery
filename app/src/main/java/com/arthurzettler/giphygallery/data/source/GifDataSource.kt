package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Gif

interface GifDataSource {
    suspend fun getTrendingGifs(): List<Gif>
    suspend fun getGifsForSearchQuery(query: String): List<Gif>
}
