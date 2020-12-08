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
        val page = 0
        val expectedFavoriteGifList = listOf(Gif("1","https://gif-url.com/1", true))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getTrendingGifs(page) } returns Result.Success(expectedGifList)
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)

        gifViewModel.load()

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
        assertThat(gifViewModel.favoriteGifList.getOrAwaitValue()).isEqualTo(expectedFavoriteGifList)
    }

    @Test
    fun `should set favorite gifs on gif list when gifs are loaded`() = runBlockingTest {
        val page = 0
        val expectedFavoriteGifList = listOf(Gif("1","https://gif-url.com/1", true))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getTrendingGifs(page) } returns Result.Success(expectedGifList)
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)

        gifViewModel.load()

        val loadedGifs = gifViewModel.gifList.getOrAwaitValue()

        assertTrue(loadedGifs[0].isFavorited)
        assertFalse(loadedGifs[1].isFavorited)
    }

    @Test
    fun `should reset has error when load is called`() = runBlockingTest {
        val page = 0
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getTrendingGifs(page) } returns result

        gifViewModel.load()

        assertFalse("Has error should be false", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when gif loading fails`() = runBlockingTest {
        val page = 0
        val expectedFavoriteGifList = listOf(Gif("1","https://gif-url.com/1", true))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)
        coEvery { mockRepository.getTrendingGifs(page) } returns Result.Failure

        gifViewModel.load()

        assertTrue("Has error should be true", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when favorite gif loading fails and not load trending gifs`() = runBlockingTest {
        val page = 0
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Failure

        gifViewModel.load()

        coVerify { mockRepository.getTrendingGifs(page) wasNot Called }
        assertTrue("Has error should be true", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should notify gif list live data when gifs are loaded from search`() = runBlockingTest {
        val page = 0
        val query = "fun"
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getGifsForSearchQuery(query, page) } returns result

        gifViewModel.search(query)

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should set favorite gifs on gif list when gifs are search`() {
        val page = 0
        val query = "fun"
        val expectedFavoriteGifList = listOf(Gif("3","https://gif-url.com/1", true))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val expectedSearchedGifList = listOf(Gif("3","https://gif-url.com/1"))

        coEvery { mockRepository.getTrendingGifs(page) } returns Result.Success(expectedGifList)
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(expectedFavoriteGifList)
        coEvery { mockRepository.getGifsForSearchQuery(query, page) } returns Result.Success(expectedSearchedGifList)

        gifViewModel.load()
        gifViewModel.search(query)

        val searchedGifs = gifViewModel.gifList.getOrAwaitValue()
        assertTrue(searchedGifs[0].isFavorited)
    }

    @Test
    fun `should reset has error when search is called`() = runBlockingTest {
        val page = 0
        val query = "fun"
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val result = Result.Success(expectedGifList)

        coEvery { mockRepository.getGifsForSearchQuery(query, page) } returns result

        gifViewModel.search(query)

        assertFalse("Has error should be false", gifViewModel.hasError.getOrAwaitValue())
    }

    @Test
    fun `should set has error when gif loading fails during search`() = runBlockingTest {
        val page = 0
        val query = "fun"
        coEvery { mockRepository.getGifsForSearchQuery(query, page) } returns Result.Failure

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
        val page = 0
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1", true), Gif("2","https://gif-url.com/2"))
        val gifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(emptyList())
        coEvery { mockRepository.getTrendingGifs(page) } returns Result.Success(gifList)

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

    @Test
    fun `should get more trending gifs when query is empty in pagination and add to current list`() = runBlockingTest {
        val loadGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val paginationGifList = listOf(Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"), Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(emptyList())
        coEvery { mockRepository.getTrendingGifs(0) } returns Result.Success(loadGifList)
        coEvery { mockRepository.getTrendingGifs(25) } returns Result.Success(paginationGifList)

        gifViewModel.load()
        gifViewModel.paginate("")

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should get more trending gifs when query is empty in pagination and add to current list verifying favorites`() = runBlockingTest {
        val favoriteGifList = listOf(Gif("4","https://gif-url.com/1", true))
        val loadGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val paginationGifList = listOf(Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(favoriteGifList)
        coEvery { mockRepository.getTrendingGifs(0) } returns Result.Success(loadGifList)
        coEvery { mockRepository.getTrendingGifs(25) } returns Result.Success(paginationGifList)

        gifViewModel.load()
        gifViewModel.paginate("")

        val gifList = gifViewModel.gifList.getOrAwaitValue()
        assertTrue(gifList[3].isFavorited)
    }

    @Test
    fun `should get trending gifs from 25 to 25 when query is empty`() = runBlockingTest {
        val secondGifList = listOf(Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))
        val firstExpectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getTrendingGifs(25) } returns Result.Success(firstExpectedGifList)
        coEvery { mockRepository.getTrendingGifs(50) } returns Result.Success(secondGifList)

        gifViewModel.paginate("")
        gifViewModel.paginate("")

        coVerifyOrder {
            mockRepository.getTrendingGifs(25)
            mockRepository.getTrendingGifs(50)
        }
    }

    @Test
    fun `should get more searched gifs when query is empty in pagination and add to current list`() = runBlockingTest {
        val query = "fun"
        val loadGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val paginationGifList = listOf(Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"), Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(emptyList())
        coEvery { mockRepository.getTrendingGifs(0) } returns Result.Success(loadGifList)
        coEvery { mockRepository.getGifsForSearchQuery(query, 25) } returns Result.Success(paginationGifList)

        gifViewModel.load()
        gifViewModel.paginate(query)

        assertThat(gifViewModel.gifList.getOrAwaitValue()).isEqualTo(expectedGifList)
    }

    @Test
    fun `should get more searched gifs when query is empty in pagination and add to current list verifying favorites`() = runBlockingTest {
        val query = "fun"
        val favoriteGifList = listOf(Gif("4","https://gif-url.com/1", true))
        val loadGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))
        val paginationGifList = listOf(Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))

        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(favoriteGifList)
        coEvery { mockRepository.getTrendingGifs(0) } returns Result.Success(loadGifList)
        coEvery { mockRepository.getGifsForSearchQuery(query, 25) } returns Result.Success(paginationGifList)

        gifViewModel.load()
        gifViewModel.paginate(query)

        val gifList = gifViewModel.gifList.getOrAwaitValue()
        assertTrue(gifList[3].isFavorited)
    }

    @Test
    fun `should get searched gifs from 25 to 25 when query is empty`() = runBlockingTest {
        val query = "fun"
        val secondGifList = listOf(Gif("3","https://gif-url.com/1"), Gif("4","https://gif-url.com/2"))
        val firstExpectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        coEvery { mockRepository.getGifsForSearchQuery(query, 25) } returns Result.Success(firstExpectedGifList)
        coEvery { mockRepository.getGifsForSearchQuery(query, 50) } returns Result.Success(secondGifList)

        gifViewModel.paginate(query)
        gifViewModel.paginate(query)

        coVerifyOrder {
            mockRepository.getGifsForSearchQuery(query, 25)
            mockRepository.getGifsForSearchQuery(query,50)
        }
    }

    @Test
    fun `should reset page when load is called`() = runBlockingTest {
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(listOf())
        coEvery { mockRepository.getTrendingGifs(0) } returns Result.Success(listOf())
        coEvery { mockRepository.getTrendingGifs(25) } returns Result.Success(listOf())

        gifViewModel.paginate("")
        gifViewModel.load()
        gifViewModel.paginate("")

        coVerifyOrder {
            mockRepository.getTrendingGifs(25)
            mockRepository.getTrendingGifs(0)
            mockRepository.getTrendingGifs(25)
        }
    }

    @Test
    fun `should reset page when search is called`() = runBlockingTest {
        coEvery { mockRepository.getFavoriteGifs() } returns Result.Success(listOf())
        coEvery { mockRepository.getGifsForSearchQuery("", 0) } returns Result.Success(listOf())
        coEvery { mockRepository.getTrendingGifs(0) } returns Result.Success(listOf())
        coEvery { mockRepository.getTrendingGifs(25) } returns Result.Success(listOf())

        gifViewModel.paginate("")
        gifViewModel.search("")
        gifViewModel.paginate("")

        coVerifyOrder {
            mockRepository.getTrendingGifs(25)
            mockRepository.getGifsForSearchQuery("",0)
            mockRepository.getTrendingGifs(25)
        }
    }
}