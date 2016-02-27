package me.rei_m.hbfavmaterial.views.widgets.manager

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.MainPageDisplayEvent
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

        adapter = BookmarkPagerAdaptor(supportFragmentManager)

        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                EventBusHolder.EVENT_BUS.post(MainPageDisplayEvent(BookmarkPagerAdaptor.BookmarkPage.values()[position]))
            }
        })
    }

    /**
     * 表示中のページが表示されたイベントをPOSTする.
     */
    fun postCurrentPageDisplayEvent() {
        EventBusHolder.EVENT_BUS.post(MainPageDisplayEvent(BookmarkPagerAdaptor.BookmarkPage.values()[currentItem]))
    }
}
