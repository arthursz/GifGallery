package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Gif
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class GifRepositoryImplTest {

    @Test
    fun `should get trending gifs from remote data source`() = runBlockingTest {
        val expectedGifList = listOf(Gif("https://gif-url.com/1"), Gif("https://gif-url.com/2"))
        val remoteDataSource = mockk<GifDataSource>(relaxed = true)
        val gifRepositoryImpl = GifRepositoryImpl(remoteDataSource)

        coEvery { remoteDataSource.getTrendingGifs() } returns expectedGifList

        val gifList = gifRepositoryImpl.getTrendingGifs()

        coVerify { remoteDataSource.getTrendingGifs() }
        assertThat(gifList).isEqualTo(expectedGifList)
    }
}