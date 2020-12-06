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
        private val gifList: List<Gif>,
        private val interaction: GifListInteraction
) : RecyclerView.Adapter<GifListAdapter.ViewHolder>() {

    class ViewHolder(gifView: View) : RecyclerView.ViewHolder(gifView) {
        var gifContainerView: ImageView = gifView.findViewById(R.id.gif_container)
        var gifFavoriteButton: ImageView = gifView.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val gifView = LayoutInflater.from(parent.context)
                .inflate(R.layout.gif_list_element, parent, false)

        return ViewHolder(gifView)
    }

    override fun getItemCount() = gifList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gif = gifList[position]

        holder.gifFavoriteButton.isSelected = gif.isFavorited
        holder.gifFavoriteButton.setOnClickListener { view ->
            interaction.onGifFavoriteStatusChanged(position, view?.isSelected?.not() ?: false)
        }

        Glide.with(context)
            .asGif()
            .load(gif.url)
            .placeholder(R.drawable.ic_animated_progress_indicator)
            .into(holder.gifContainerView)
    }
}