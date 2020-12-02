package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Gif

class GifRepositoryImpl(private val remoteDataSource: GifDataSource = GifRemoteDataSource()) : GifRepository {
    override suspend fun getTrendingGifs(): List<Gif> {
        return remoteDataSource.getTrendingGifs()
    }
}