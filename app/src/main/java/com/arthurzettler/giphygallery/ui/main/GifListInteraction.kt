package com.arthurzettler.giphygallery.ui.main

interface GifListInteraction {
    fun onGifFavoriteStatusChanged(position: Int, isFavorite: Boolean)
}