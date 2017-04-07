package me.rei_m.hbfavmaterial.presentation.widget.viewpager

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter

/**
 * MainActivityのメインコンテンツを管理するViewPager.
 */
class BookmarkViewPager @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    fun initialize(supportFragmentManager: FragmentManager) {

        offscreenPageLimit = BookmarkPagerAdapter.Page.values().size

        adapter = BookmarkPagerAdapter(supportFragmentManager)
    }
}
