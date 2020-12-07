package com.arthurzettler.giphygallery.ui.main

import com.arthurzettler.giphygallery.data.Gif

interface GifListInteraction {
    fun onGifFavoriteStatusChanged(gif: Gif, isFavorite: Boolean)
}