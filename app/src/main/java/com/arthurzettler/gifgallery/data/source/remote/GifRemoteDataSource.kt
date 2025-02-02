package com.arthurzettler.gifgallery.data.source.remote

import com.arthurzettler.gifgallery.data.Gif
import com.arthurzettler.gifgallery.data.source.GifDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception

class GifRemoteDataSource(
    private val config: RemoteConfig = RemoteConfig(),
    private val client: OkHttpClient = OkHttpClient()
): GifDataSource {
    override suspend fun getFavoriteGifs() = listOf<Gif>()
    override suspend fun storeGif(gif: Gif) {}
    override suspend fun removeGif(gifId: String) {}

    override suspend fun getTrendingGifs(page: Int) = withContext(Dispatchers.IO) {
        getGifs(TRENDING_URL.format(config.getApiKey(), page))
    }

    override suspend fun getGifsForSearchQuery(query: String, page: Int) = withContext(Dispatchers.IO) {
        getGifs(SEARCH_URL.format(config.getApiKey(), query, page))
    }

    private fun getGifs(url: String): List<Gif> {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val responseBody = response.body

        if (response.isSuccessful.not() || responseBody == null) throw Exception(response.message)

        return parseResponse(responseBody.string())
    }

    private fun parseResponse(body: String): List<Gif> {
        val gifList = mutableListOf<Gif>()
        val json = JSONObject(body)

        if (json.has("data").not()) throw Exception("Unable to parse response")

        val dataArray = json.getJSONArray("data")

        (0 until dataArray.length()).forEach { index ->
            val gifData = dataArray.getJSONObject(index)
            val id = gifData.getString("id")
            val url = gifData
                .optJSONObject("images")
                ?.optJSONObject("original")
                ?.optString("url") ?: return@forEach

            gifList.add(Gif(id, url))
        }

        return gifList
    }

    companion object {
        private const val TRENDING_URL = "https://api.giphy.com/v1/gifs/trending?api_key=%s&limit=25&offset=%s"
        private const val SEARCH_URL = "https://api.giphy.com/v1/gifs/search?api_key=%s&q=%s&limit=25&offset=%s"
    }
}