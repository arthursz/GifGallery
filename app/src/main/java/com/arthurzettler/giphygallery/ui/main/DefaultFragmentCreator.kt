package com.arthurzettler.giphygallery.ui.main

import androidx.fragment.app.Fragment

interface DefaultFragmentCreator {
    val titleId: Int
    fun newInstance(): Fragment
}