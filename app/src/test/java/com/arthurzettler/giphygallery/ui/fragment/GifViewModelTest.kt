package com.arthurzettler.giphygallery.ui.fragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.source.GifRepository
import com.arthurzettler.giphygallery.getOrAwaitValue
import com.arthurzettler.giphygallery.ui.fragment.GifViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
    fun `should notify gif list live data when gifs are loaded`() = runBlockingTest {
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getTrendingGifs() } returns result

        gifViewModel.load()

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
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
        coEvery { mockRepository.getTrendingGifs() } returns Result.Failure

        gifViewModel.load()

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

        coEvery { mockRepository.getTrendingGifs() } returns Result.Success(gifList)

        gifViewModel.load()
        gifViewModel.setFavoriteGif(Gif("1","https://gif-url.com/1"), true)

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }
}