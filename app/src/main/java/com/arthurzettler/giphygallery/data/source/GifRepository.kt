package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.Gif

interface GifRepository {
    suspend fun getTrendingGifs() : Result<List<Gif>>
}