package com.arthurzettler.gifgallery.data.source

import com.arthurzettler.gifgallery.data.Result
import com.arthurzettler.gifgallery.data.Gif
import com.arthurzettler.gifgallery.data.source.local.GifLocalDataSource
import com.arthurzettler.gifgallery.data.source.remote.GifRemoteDataSource
import java.lang.Exception

class GifRepositoryImpl(
    private val remoteDataSource: GifDataSource = GifRemoteDataSource(),
    private val localDataSource: GifDataSource = GifLocalDataSource()
) : GifRepository {
    override suspend fun getTrendingGifs(): Result<List<Gif>> =
        try {
            Result.Success(remoteDataSource.getTrendingGifs())
        } catch (error: Exception) {
            Result.Failure
        }

    override suspend fun getGifsForSearchQuery(query: String): Result<List<Gif>> =
        try {
            Result.Success(remoteDataSource.getGifsForSearchQuery(query))
        } catch (error: Exception) {
            Result.Failure
        }

    override suspend fun getFavoriteGifs(): Result<List<Gif>> =
        try {
            Result.Success(localDataSource.getFavoriteGifs())
        } catch (error: Exception) {
            Result.Failure
        }

    override suspend fun storeFavoriteGif(gif: Gif) { localDataSource.storeGif(gif) }

    override suspend fun removeFavoriteGif(gifId: String) { localDataSource.removeGif(gifId) }
}