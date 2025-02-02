package com.arthurzettler.gifgallery.data.source

import com.arthurzettler.gifgallery.data.Gif
import com.arthurzettler.gifgallery.data.Result
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

    @RelaxedMockK
    private lateinit var mockLocalDataSource: GifDataSource

    private lateinit var gifRepository : GifRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        gifRepository = GifRepositoryImpl(mockRemoteDataSource, mockLocalDataSource)
    }

    @Test
    fun `should get trending gifs from remote data source`() = runBlockingTest {
        val page = 0
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val expectedResult = Result.Success(expectedGifList)

        coEvery { mockRemoteDataSource.getTrendingGifs(page) } returns expectedGifList

        val result = gifRepository.getTrendingGifs(page)

        coVerify { mockRemoteDataSource.getTrendingGifs(page) }
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should return result failure when error happens getting trending gifs`() = runBlockingTest {
        val page = 0
        coEvery { mockRemoteDataSource.getTrendingGifs(page) } throws Exception()

        val result = gifRepository.getTrendingGifs(page)

        assertThat(result).isEqualTo(Result.Failure)
    }

    @Test
    fun `should get searched gifs from remote data source`() = runBlockingTest {
        val page = 0
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val expectedResult = Result.Success(expectedGifList)
        val query = "fun"

        coEvery { mockRemoteDataSource.getGifsForSearchQuery(query, page) } returns expectedGifList

        val result = gifRepository.getGifsForSearchQuery(query, page)

        coVerify { mockRemoteDataSource.getGifsForSearchQuery(query, page) }
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should return result failure when error happens getting searched gifs`() = runBlockingTest {
        val page = 0
        val query = "fun"

        coEvery { mockRemoteDataSource.getGifsForSearchQuery(query, page) } throws Exception()

        val result = gifRepository.getGifsForSearchQuery(query, page)

        assertThat(result).isEqualTo(Result.Failure)
    }

    @Test
    fun `should get favorite gifs from local data source`() = runBlockingTest {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1", true), Gif("2","https://gif-url.com/2", true))
        val expectedResult = Result.Success(expectedGifList)

        coEvery { mockLocalDataSource.getFavoriteGifs() } returns expectedGifList

        val result = gifRepository.getFavoriteGifs()

        coVerify { mockLocalDataSource.getFavoriteGifs() }
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `should return result failure when error happens getting favorite gifs`() = runBlockingTest {
        coEvery { mockLocalDataSource.getFavoriteGifs() } throws Exception()

        val result = gifRepository.getFavoriteGifs()

        assertThat(result).isEqualTo(Result.Failure)
    }

    @Test
    fun `should store favorite gif on local data source`() = runBlockingTest {
        val expectedGif = Gif("1", "https://gif-url.com/1", true)

        gifRepository.storeFavoriteGif(expectedGif)

        coVerify { mockLocalDataSource.storeGif(expectedGif) }
    }

    @Test
    fun `should remove favorite gif from local data source`() = runBlockingTest {
        val expectedGifId = "1234"

        gifRepository.removeFavoriteGif(expectedGifId)

        coVerify { mockLocalDataSource.removeGif(expectedGifId) }
    }
}