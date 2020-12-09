package com.arthurzettler.gifgallery.ui.fragment

import android.content.ContentValues
import android.content.Intent
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

open class ShareFragment : Fragment() {
    fun shareGif (gif: ByteBuffer) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Gif")
            put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
        }
        val uri = activity?.contentResolver?.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: return

        try {
            val output = activity?.contentResolver?.openOutputStream(uri) as FileOutputStream
            val bytes = ByteArray(gif.capacity())
            (gif.duplicate().clear() as ByteBuffer).get(bytes)
            output.write(bytes, 0 ,bytes.size)
            output.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val share = Intent(Intent.ACTION_SEND).apply {
            type = "image/gif"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(share, "Share Gif"))
    }
}