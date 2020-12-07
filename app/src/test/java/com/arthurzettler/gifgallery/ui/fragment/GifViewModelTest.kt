package com.arthurzettler.gifgallery.ui.fragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arthurzettler.gifgallery.data.Gif
import com.arthurzettler.gifgallery.data.Result
import com.arthurzettler.gifgallery.data.source.GifRepository
import com.arthurzettler.gifgallery.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GifViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var mockRepository: GifRepository

    private lateinit var gifViewModel: GifViewModel

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(testCoroutineDispatcher)

        gifViewModel = GifViewModel(mockRepository)
    }

    @Test
    fun `should notify gif list and favorite gif list live data when gifs are loaded`() = runBlockingTest {
        val expectedFavoriteGifList = listOf(Gif("1","https://gif-url.com/1", true))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getTrendingGifs() } returns Result.Success(expectedGifList)
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)

        gifViewModel.load()

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
        assertThat(gifViewModel.favoriteGifList.getOrAwaitValue()).isEqualTo(expectedFavoriteGifList)
    }

    @Test
    fun `should set favorite gifs on gif list when gifs are loaded`() = runBlockingTest {
        val expectedFavoriteGifList = listOf(Gif("1","https://gif-url.com/1", true))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getTrendingGifs() } returns Result.Success(expectedGifList)
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)

        gifViewModel.load()

        val loadedGifs = gifViewModel.gifList.getOrAwaitValue()

        assertTrue(loadedGifs[0].isFavorited)
        assertFalse(loadedGifs[1].isFavorited)
    }

    @Test
    fun `should reset has error when load is called`() = runBlockingTest {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getTrendingGifs() } returns result

        gifViewModel.load()

        assertFalse("Has error should be false", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when gif loading fails`() = runBlockingTest {
        val expectedFavoriteGifList = listOf(Gif("1","https://gif-url.com/1", true))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)
        coEvery { mockRepository.getTrendingGifs() } returns Result.Failure

        gifViewModel.load()

        assertTrue("Has error should be true", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when favorite gif loading fails and not load trending gifs`() = runBlockingTest {
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Failure

        gifViewModel.load()

        coVerify { mockRepository.getTrendingGifs() wasNot Called }
        assertTrue("Has error should be true", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should notify gif list live data when gifs are loaded from search`() = runBlockingTest {
        val query = "fun"
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getGifsForSearchQuery(query) } returns result

        gifViewModel.search(query)

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should set favorite gifs on gif list when gifs are search`() {
        val query = "fun"
        val expectedFavoriteGifList = listOf(Gif("3","https://gif-url.com/1", true))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val expectedSearchedGifList = listOf(Gif("3","https://gif-url.com/1"))

        coEvery { mockRepository.getTrendingGifs() } returns Result.Success(expectedGifList)
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)
        coEvery { mockRepository.getGifsForSearchQuery(query) } returns Result.Success(expectedSearchedGifList)

        gifViewModel.load()
        gifViewModel.search(query)

        val searchedGifs = gifViewModel.gifList.getOrAwaitValue()
        assertTrue(searchedGifs[0].isFavorited)
    }

    @Test
    fun `should reset has error when search is called`() = runBlockingTest {
        val query = "fun"
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getGifsForSearchQuery(query) } returns result

        gifViewModel.search(query)

        assertFalse("Has error should be false", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when gif loading fails during search`() = runBlockingTest {
        val query = "fun"
        coEvery { mockRepository.getGifsForSearchQuery(query) } returns Result.Failure

        gifViewModel.search(query)

        assertTrue("Has error should be true", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should add favorite gif to favorite gif list`() {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1", true))

        gifViewModel.setFavoriteGif(Gif("1", "https://gif-url.com/1"), true)

        assertThat(gifViewModel.favoriteGifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should remove favorite gif from favorite gif list`() {
        val gif = Gif("1", "https://gif-url.com/1")

        gifViewModel.setFavoriteGif(gif, true)
        gifViewModel.setFavoriteGif(gif, false)

        assertThat(gifViewModel.favoriteGifList.getOrAwaitValue()).isEqualTo(mutableListOf<Gif>())
    }

    @Test
    fun `should set gif as favorite in gif list`() = runBlockingTest {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1", true), Gif("2","https://gif-url.com/2"))
        val gifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(emptyList())
        coEvery { mockRepository.getTrendingGifs() } returns Result.Success(gifList)

        gifViewModel.load()
        gifViewModel.setFavoriteGif(Gif("1","https://gif-url.com/1"), true)

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should store favorite gif on repository`() = runBlockingTest {
        val gif = Gif("1", "https://gif-url.com/1", true)

        gifViewModel.setFavoriteGif(gif, true)

        coVerify { mockRepository.storeFavoriteGif(gif) }
    }

    @Test
    fun `should remove favorite gif from repository`() = runBlockingTest {
        val gif = Gif("1", "https://gif-url.com/1", false)

        gifViewModel.setFavoriteGif(gif, false)

        coVerify { mockRepository.removeFavoriteGif(gif.id) }
    }
}