package com.arthurzettler.gifgallery.ui.fragment

import com.arthurzettler.gifgallery.data.Gif

interface GifListInteraction {
    fun onGifFavoriteStatusChanged(gif: Gif, isFavorite: Boolean)
}