package com.arthurzettler.giphygallery.ui.fragment.trending

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.ui.fragment.DefaultFragmentCreator
import com.arthurzettler.giphygallery.ui.fragment.GifListAdapter
import com.arthurzettler.giphygallery.ui.fragment.GifListInteraction
import com.arthurzettler.giphygallery.ui.fragment.GifViewModel

class TrendingFragment : Fragment(), GifListInteraction {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicatorView: ProgressBar
    private lateinit var errorView: ViewGroup
    private lateinit var retryButton: Button

    private val viewModel: GifViewModel by activityViewModels()
    private val gifList: MutableList<Gif> = mutableListOf()
    private var currentQuery = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.trending_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        loadGifList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)

        val searchView = SearchView(context).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    if (query.isEmpty()) {
                        loadGifList()
                        currentQuery = ""
                    }
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    if (query.isEmpty().not()) searchGifs(query)
                    currentQuery = query
                    return false
                }
            })
            if (currentQuery.isEmpty().not()) {
                setQuery(currentQuery, false)
                openSearchView() 
            }
        }

        menu.findItem(R.id.search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }
    }

    override fun onGifFavoriteStatusChanged(gif: Gif, isFavorite: Boolean) {
        viewModel.setFavoriteGif(gif, isFavorite)
        viewModel.notifyFavoriteGifListObserver()
        recyclerView.adapter?.notifyItemChanged(gifList.indexOf(gif))
    }

    private fun setupView(view: View) {
        errorView = view.findViewById(R.id.error_layout)
        progressIndicatorView = view.findViewById(R.id.progress_indicator)
        recyclerView = view.findViewById<RecyclerView>(R.id.gif_list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GifListAdapter(context, gifList, this@TrendingFragment)
            itemAnimator = null
        }
        retryButton = view.findViewById<Button>(R.id.retry_button).apply {
            setOnClickListener { loadGifList() }
        }
    }

    private fun setupViewModel() {
        viewModel.gifList.observe(viewLifecycleOwner, Observer {
            setListLoaded()
            gifList.clear()
            gifList.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        })
        viewModel.hasError.observe(viewLifecycleOwner, Observer {
            if (it) setError() else setLoading()
        })
    }

    private fun loadGifList() {
        setLoading()
        viewModel.load()
    }

    private fun searchGifs(query: String) {
        setLoading()
        viewModel.search(query)
    }

    private fun setLoading() {
        progressIndicatorView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    private fun setListLoaded() {
        recyclerView.visibility = View.VISIBLE
        progressIndicatorView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    private fun setError() {
        errorView.visibility = View.VISIBLE
        progressIndicatorView.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun SearchView.openSearchView() {
        isIconified = false
        clearFocus()
    }

    companion object: DefaultFragmentCreator {
        override val titleId = R.string.trending
        override fun newInstance() = TrendingFragment()
    }
}
