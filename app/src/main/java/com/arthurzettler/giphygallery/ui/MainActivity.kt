package com.arthurzettler.giphygallery.ui

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.arthurzettler.giphygallery.R
import com.arthurzettler.giphygallery.ui.fragment.DefaultFragmentCreator
import com.arthurzettler.giphygallery.ui.fragment.favorite.FavoriteFragment
import com.arthurzettler.giphygallery.ui.fragment.trending.TrendingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val tabLayout: TabLayout by lazy { findViewById<TabLayout>(R.id.tabs) }
    private val viewPager: ViewPager2 by lazy { findViewById<ViewPager2>(R.id.view_pager) }

    private val fragments = listOf(TrendingFragment.Companion, FavoriteFragment.Companion)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        configureFragments()
    }

    private fun configureFragments() {
        viewPager.adapter = PageAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                hideKeyboardFrom()
                super.onPageSelected(position)
            }
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int) = getString(fragments[position].titleId)

    fun hideKeyboardFrom() {
        (this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(this@MainActivity.currentFocus?.windowToken, 0)
        }
    }
}

private class PageAdapter(
    fragmentActivity: FragmentActivity,
    val fragments: List<DefaultFragmentCreator>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position].newInstance()
}