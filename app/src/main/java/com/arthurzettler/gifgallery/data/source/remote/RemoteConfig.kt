package com.arthurzettler.gifgallery.data.source.remote

import com.arthurzettler.gifgallery.BuildConfig

class RemoteConfig {
    fun getApiKey(): String = BuildConfig.GIPHY_API_KEY
}