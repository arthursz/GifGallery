package com.arthurzettler.giphygallery.ui.fragment.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.ui.fragment.DefaultFragmentCreator
import com.arthurzettler.giphygallery.ui.fragment.GifListAdapter
import com.arthurzettler.giphygallery.ui.fragment.GifListInteraction
import com.arthurzettler.giphygallery.ui.fragment.GifViewModel

class FavoriteFragment : Fragment(), GifListInteraction {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicatorView: ProgressBar
    private lateinit var errorView: ViewGroup
    private lateinit var retryButton: Button

    private val viewModel: GifViewModel by activityViewModels()
    private val favoriteGifList: MutableList<Gif> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    override fun onGifFavoriteStatusChanged(gif: Gif, isFavorite: Boolean) {
        val index = favoriteGifList.indexOf(gif)
        viewModel.setFavoriteGif(gif, isFavorite)
        viewModel.updateGifList()
        favoriteGifList.removeAt(index)
        recyclerView.adapter?.notifyItemRemoved(index)
    }

    private fun setupView(view: View) {
        errorView = view.findViewById(R.id.error_layout)
        progressIndicatorView = view.findViewById(R.id.progress_indicator)
        recyclerView = view.findViewById<RecyclerView>(R.id.gif_list).apply {
            layoutManager = StaggeredGridLayoutManager(LAYOUT_COLUMNS, LinearLayoutManager.VERTICAL)
            adapter = GifListAdapter(context, favoriteGifList, this@FavoriteFragment)
        }
        retryButton = view.findViewById<Button>(R.id.retry_button).apply {
            setOnClickListener { }
        }
    }

    private fun setupViewModel() {
        viewModel.favoriteGifList.observe(viewLifecycleOwner, Observer {
            favoriteGifList.clear()
            favoriteGifList.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        })
    }

    companion object: DefaultFragmentCreator {
        override val titleId = R.string.favorites
        override fun newInstance() = FavoriteFragment()

        private const val LAYOUT_COLUMNS = 2
    }

}