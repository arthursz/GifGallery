package com.arthurzettler.gifgallery.data.source

import com.arthurzettler.gifgallery.data.Result
import com.arthurzettler.gifgallery.data.Gif
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GifRepositoryImplTest {

    @RelaxedMockK
    private lateinit var mockRemoteDataSource: GifDataSource

    private lateinit var gifRepository : GifRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        gifRepository = GifRepositoryImpl(mockRemoteDataSource)
    }

    @Test
    fun `should get trending gifs from remote data source`() = runBlockingTest {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val expectedResult = Result.Success(expectedGifList)

        coEvery { mockRemoteDataSource.getTrendingGifs() } returns expectedGifList

        val result = gifRepository.getTrendingGifs()

        coVerify { mockRemoteDataSource.getTrendingGifs() }
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should return result failure when error happens getting trending gifs`() = runBlockingTest {
        coEvery { mockRemoteDataSource.getTrendingGifs() } throws Exception()

        val result = gifRepository.getTrendingGifs()

        assertThat(result).isEqualTo(Result.Failure)
    }

    @Test
    fun `should get searched gifs from remote data source`() = runBlockingTest {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val expectedResult = Result.Success(expectedGifList)
        val query = "fun"

        coEvery { mockRemoteDataSource.getGifsForSearchQuery(query) } returns expectedGifList

        val result = gifRepository.getGifsForSearchQuery(query)

        coVerify { mockRemoteDataSource.getGifsForSearchQuery(query) }
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should return result failure when error happens getting searched gifs`() = runBlockingTest {
        val query = "fun"

        coEvery { mockRemoteDataSource.getGifsForSearchQuery(query) } throws Exception()

        val result = gifRepository.getGifsForSearchQuery(query)

        assertThat(result).isEqualTo(Result.Failure)
    }
}