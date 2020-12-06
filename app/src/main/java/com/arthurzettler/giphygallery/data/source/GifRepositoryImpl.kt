package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.Gif
import java.lang.Exception

class GifRepositoryImpl(private val remoteDataSource: GifDataSource = GifRemoteDataSource()) : GifRepository {
    override suspend fun getTrendingGifs(): Result<List<Gif>> =
        try {
            Result.Success(remoteDataSource.getTrendingGifs())
        } catch (error: Exception) {
            Result.Failure
        }
}