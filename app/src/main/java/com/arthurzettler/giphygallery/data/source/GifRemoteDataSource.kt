package com.arthurzettler.giphygallery.data.source

import com.arthurzettler.giphygallery.data.Gif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception

class GifRemoteDataSource(private val client: OkHttpClient = OkHttpClient()): GifDataSource {

    override suspend fun getTrendingGifs() = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(TRENDING_URL).build()
        val response = client.newCall(request).execute()
        val responseBody = response.body

        if (response.isSuccessful.not() || responseBody == null) throw Exception(response.message)

        return@withContext parseResponse(responseBody.string())
    }

    private fun parseResponse(body: String): List<Gif> {
        val gifList = mutableListOf<Gif>()
        val json = JSONObject(body)

        if (json.has("data").not()) throw Exception("Unable to parse response")

        val dataArray = json.getJSONArray("data")

        (0 until dataArray.length()).forEach { index ->
            val url = dataArray.getJSONObject(index)
                .optJSONObject("images")
                ?.optJSONObject("original")
                ?.optString("url")

            url?.let { gifList.add(Gif(it)) }
        }

        return gifList
    }

    companion object {
        private const val TRENDING_URL = "https://api.giphy.com/v1/gifs/trending?api_key=Vgshav6wEuIEIhpAVyPx7iMwkmlEVVmk&limit=25"
    }
}