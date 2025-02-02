package com.arthurzettler.gifgallery.data.source.remote

import com.arthurzettler.gifgallery.data.Gif
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.Call
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.Exception

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class GifRemoteDataSourceTest {

    @RelaxedMockK
    private lateinit var mockClient: OkHttpClient

    @RelaxedMockK
    private lateinit var mockCall: Call

    @RelaxedMockK
    private lateinit var mockResponse: Response

    @RelaxedMockK
    private lateinit var mockRemoteConfig: RemoteConfig

    @RelaxedMockK
    private lateinit var mockResponseBody: ResponseBody

    private lateinit var gifRemoteDataSource: GifRemoteDataSource

    private val apiKey = "teste-api-key"
    private val validJson = "{\"data\": [{ \"id\": \"1\", \"images\": {\"original\": {\"url\": \"https://gif-url.com/1\"}}},{ \"id\": \"2\", \"images\": {\"original\": {\"url\": \"https://gif-url.com/2\"}}}]}"
    private val jsonWithoutOriginalData = "{\"data\": [{ \"id\": \"1\", \"images\": {}},{ \"id\": \"2\", \"images\": {\"original\": {\"url\": \"https://gif-url.com/2\"}}}]}"

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        mockkConstructor(Request.Builder::class)
        every { mockRemoteConfig.getApiKey() } returns apiKey

        gifRemoteDataSource = GifRemoteDataSource(mockRemoteConfig, mockClient)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `should get trending gifs from server`() = runBlocking {
        val page = 25
        val expectedUrl = "https://api.giphy.com/v1/gifs/trending?api_key=$apiKey&limit=25&offset=$page"
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        every { mockResponseBody.string() } returns validJson
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns mockResponseBody
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        val gifList = gifRemoteDataSource.getTrendingGifs(page)

        verify { anyConstructed<Request.Builder>().url(expectedUrl) }
        assertThat(gifList).isEqualTo(expectedGifList)
    }

    @Test
    fun `should not fail to get trending gifs when response not have original gif data`() = runBlocking {
        val page = 0
        val expectedGifList = listOf(Gif("2","https://gif-url.com/2"))

        every { mockResponseBody.string() } returns jsonWithoutOriginalData
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns mockResponseBody
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        val gifList = gifRemoteDataSource.getTrendingGifs(page)

        assertThat(gifList).isEqualTo(expectedGifList)
    }

    @Test
    fun `should throw exception when response is not successful`() = runBlocking {
        val page = 0
        val expectedException = Exception("Response not successful")

        every { mockResponse.message } returns "Response not successful"
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.body } returns mockResponseBody
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        try {
            gifRemoteDataSource.getTrendingGifs(page)
            fail("Should throw exception when response is not successful")
        } catch (exception: Exception) {
            assertThat(exception.message).isEqualTo(expectedException.message)
        }
    }

    @Test
    fun `should throw exception when response body is null`() = runBlocking {
        val page = 0
        val expectedException = Exception("Response not successful")

        every { mockResponse.message } returns "Response not successful"
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns null
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        try {
            gifRemoteDataSource.getTrendingGifs(page)
            fail("Should throw exception when response body is null")
        } catch (exception: Exception) {
            assertThat(exception.message).isEqualTo(expectedException.message)
        }
    }

    @Test
    fun `should get searched gifs from server`() = runBlocking {
        val page = 25
        val query = "fun"
        val expectedUrl = "https://api.giphy.com/v1/gifs/search?api_key=$apiKey&q=$query&limit=25&offset=$page"
        val expectedGifList = listOf(Gif("1","https://gif-url.com/1"), Gif("2","https://gif-url.com/2"))

        every { mockResponseBody.string() } returns validJson
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns mockResponseBody
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        val gifList = gifRemoteDataSource.getGifsForSearchQuery(query, page)

        verify { anyConstructed<Request.Builder>().url(expectedUrl) }
        assertThat(gifList).isEqualTo(expectedGifList)
    }

    @Test
    fun `should not fail to get searched gifs when response not have original gif data`() = runBlocking {
        val page = 0
        val expectedGifList = listOf(Gif("2","https://gif-url.com/2"))

        every { mockResponseBody.string() } returns jsonWithoutOriginalData
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns mockResponseBody
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        val gifList = gifRemoteDataSource.getGifsForSearchQuery("fun", page)

        assertThat(gifList).isEqualTo(expectedGifList)
    }

    @Test
    fun `should throw exception when response is not successful getting searched gifs`() = runBlocking {
        val page = 0
        val expectedException = Exception("Response not successful")

        every { mockResponse.message } returns "Response not successful"
        every { mockResponse.isSuccessful } returns false
        every { mockResponse.body } returns mockResponseBody
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        try {
            gifRemoteDataSource.getGifsForSearchQuery("fun", page)
            fail("Should throw exception when response is not successful")
        } catch (exception: Exception) {
            assertThat(exception.message).isEqualTo(expectedException.message)
        }
    }

    @Test
    fun `should throw exception when response body is null getting searched gifs`() = runBlocking {
        val page = 0
        val expectedException = Exception("Response not successful")

        every { mockResponse.message } returns "Response not successful"
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns null
        every { mockCall.execute() } returns mockResponse
        every { mockClient.newCall(any()) } returns mockCall

        try {
            gifRemoteDataSource.getGifsForSearchQuery("fun", page)
            fail("Should throw exception when response body is null")
        } catch (exception: Exception) {
            assertThat(exception.message).isEqualTo(expectedException.message)
        }
    }
}