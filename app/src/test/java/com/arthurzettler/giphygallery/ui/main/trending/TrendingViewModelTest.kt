package com.arthurzettler.giphygallery.ui.main.trending

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.data.Result
import com.arthurzettler.giphygallery.data.source.GifRepository
import com.arthurzettler.giphygallery.getOrAwaitValue
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
class TrendingViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var mockRepository: GifRepository

    private lateinit var trendingViewModel: TrendingViewModel

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(testCoroutineDispatcher)

        trendingViewModel = TrendingViewModel(mockRepository)
    }

    @Test
    fun `should notify gif list live data when gifs are loaded`() = runBlockingTest {
        val expectedGifList = listOf(Gif("https://gif-url.com/1"), Gif("https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getTrendingGifs() } returns result

        trendingViewModel.load()

        assertThat(trendingViewModel.giftList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should reset has error when load is called`() = runBlockingTest {
        val expectedGifList = listOf(Gif("https://gif-url.com/1"), Gif("https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getTrendingGifs() } returns result

        trendingViewModel.load()

        assertFalse("Has error should be false", trendingViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when gif loading fails`() = runBlockingTest {
        coEvery { mockRepository.getTrendingGifs() } returns Result.Failure

        trendingViewModel.load()

        assertTrue("Has error should be true", trendingViewModel.hasError.getOrAwaitValue())
    }
}