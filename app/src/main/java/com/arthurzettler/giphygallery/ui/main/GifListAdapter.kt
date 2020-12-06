package com.arthurzettler.giphygallery.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.data.Gif
import com.bumptech.glide.Glide

class GifListAdapter(
        private val context: Context,
        private val gifList: List<Gif>
) : RecyclerView.Adapter<GifListAdapter.ViewHolder>() {

    class ViewHolder(gifView: View) : RecyclerView.ViewHolder(gifView) {
        var gifContainerView: ImageView = gifView.findViewById(R.id.gif_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val gifView = LayoutInflater.from(parent.context)
                .inflate(R.layout.gif_list_element, parent, false)

        return ViewHolder(gifView)
    }

    override fun getItemCount() = gifList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gifUrl = gifList[position].url

        Glide.with(context)
            .asGif()
            .load(gifUrl)
            .placeholder(android.R.drawable.stat_sys_download)
            .into(holder.gifContainerView)
    }
}