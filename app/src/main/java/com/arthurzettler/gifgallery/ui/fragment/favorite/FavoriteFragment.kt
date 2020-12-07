package com.arthurzettler.gifgallery.ui.fragment.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arthurzettler.gifgallery.R
import com.arthurzettler.gifgallery.data.Gif
import com.arthurzettler.gifgallery.ui.fragment.DefaultFragmentCreator
import com.arthurzettler.gifgallery.ui.fragment.GifListAdapter
import com.arthurzettler.gifgallery.ui.fragment.GifListInteraction
import com.arthurzettler.gifgallery.ui.fragment.GifViewModel

class FavoriteFragment : Fragment(), GifListInteraction {

    private lateinit var recyclerView: RecyclerView

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
        viewModel.notifyGifListObserver()
        favoriteGifList.removeAt(index)
        recyclerView.adapter?.notifyItemRemoved(index)
    }

    private fun setupView(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.gif_list).apply {
            layoutManager = StaggeredGridLayoutManager(LAYOUT_COLUMNS, LinearLayoutManager.VERTICAL)
            adapter = GifListAdapter(context, favoriteGifList, this@FavoriteFragment)
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