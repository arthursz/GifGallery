package com.arthurzettler.giphygallery.ui.main.trending

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.ui.main.DefaultFragmentCreator
import com.arthurzettler.giphygallery.ui.main.GifListAdapter
import com.arthurzettler.giphygallery.ui.main.GifListInteraction

class TrendingFragment : Fragment(), GifListInteraction {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressIndicatorView: ProgressBar
    private lateinit var errorView: ViewGroup
    private lateinit var retryButton: Button

    private lateinit var viewModel: TrendingViewModel
    private val gifList: MutableList<Gif> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
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

    override fun onGifFavoriteStatusChanged(position: Int, isFavorite: Boolean) {
        gifList[position].isFavorited = isFavorite
        recyclerView.adapter?.notifyItemChanged(position)
    }

    private fun setupView(view: View) {
        errorView = view.findViewById(R.id.error_layout)
        progressIndicatorView = view.findViewById(R.id.progress_indicator)
        recyclerView = view.findViewById<RecyclerView>(R.id.gif_list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GifListAdapter(context, gifList, this@TrendingFragment)
        }
        retryButton = view.findViewById<Button>(R.id.retry_button).apply {
            setOnClickListener { loadGifList() }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(TrendingViewModel::class.java)
        viewModel.giftList.observe(viewLifecycleOwner, Observer {
            setListLoaded()
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

    companion object: DefaultFragmentCreator {
        override val titleId = R.string.trending
        override fun newInstance() = TrendingFragment()
    }
}
