package com.arthurzettler.giphygallery.ui.main.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.ui.main.DefaultFragmentCreator
import com.arthurzettler.giphygallery.ui.main.trending.TrendingViewModel

class FavoriteFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
    }

    companion object: DefaultFragmentCreator {
        override val titleId = R.string.favorites
        override fun newInstance() = FavoriteFragment()
    }

}