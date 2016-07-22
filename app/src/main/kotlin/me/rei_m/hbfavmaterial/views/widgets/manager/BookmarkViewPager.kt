package me.rei_m.hbfavmaterial.views.widgets.manager

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor

/**
 * MainActivityのメインコンテンツを管理するViewPager.
 */
class BookmarkViewPager : ViewPager {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 初期設定を行う.
     */
    fun initialize(supportFragmentManager: FragmentManager) {

        offscreenPageLimit = BookmarkPagerAdaptor.Page.values().size

        adapter = BookmarkPagerAdaptor(supportFragmentManager)
    }
}
