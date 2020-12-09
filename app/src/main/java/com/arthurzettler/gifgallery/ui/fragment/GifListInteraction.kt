package com.arthurzettler.gifgallery.ui.fragment

import com.arthurzettler.gifgallery.data.Gif
import java.nio.ByteBuffer

interface GifListInteraction {
    fun onGifFavoriteStatusChanged(gif: Gif, isFavorite: Boolean)
    fun onShareGif(gif: ByteBuffer)
}