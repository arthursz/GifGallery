package com.arthurzettler.gifgallery.data.source.local

import com.arthurzettler.gifgallery.data.Gif
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GifLocalDataSourceTest {

    @RelaxedMockK
    private lateinit var mockDatabase: GifDatabase

    @RelaxedMockK
    private lateinit var mockGifDao: GifDao

    private lateinit var gifLocalDataSource: GifLocalDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { mockDatabase.gifDao() } returns mockGifDao

        gifLocalDataSource = GifLocalDataSource(mockDatabase)
    }

    @Test
    fun `should get gifs from database`() = runBlocking {
        gifLocalDataSource.getFavoriteGifs()

        verify { mockGifDao.getGifs() }
    }

    @Test
    fun `should store gif on database`() = runBlocking {
        val gif = Gif("1", "http://test.com/")

        gifLocalDataSource.storeGif(gif)

        verify { mockGifDao.insertGif(gif) }
    }

    @Test
    fun `should remove gif from database`() = runBlocking {
        val id = "1234"

        gifLocalDataSource.removeGif(id)

        verify { mockGifDao.removeGif(id) }
    }
}