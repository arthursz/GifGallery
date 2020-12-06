package com.arthurzettler.giphygallery.ui.main.trending

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.data.Gif
import com.arthurzettler.giphygallery.ui.main.DefaultFragmentCreator
import com.arthurzettler.giphygallery.ui.main.GifListAdapter

class TrendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var viewModel: TrendingViewModel
    private val gifList: MutableList<Gif> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.trending_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(TrendingViewModel::class.java)
        viewModel.giftList.observe(viewLifecycleOwner, Observer {
            gifList.addAll(it)
            recyclerView.adapter?.notifyDataSetChanged()
        })
        viewModel.load()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.gif_list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GifListAdapter(context, gifList)
        }
    }

    companion object: DefaultFragmentCreator {
        override val titleId = R.string.trending
        override fun newInstance() = TrendingFragment()
    }
}
