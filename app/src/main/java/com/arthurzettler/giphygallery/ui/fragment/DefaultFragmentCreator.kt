package com.arthurzettler.giphygallery.ui.fragment

import androidx.fragment.app.Fragment

interface DefaultFragmentCreator {
    val titleId: Int
    fun newInstance(): Fragment
}