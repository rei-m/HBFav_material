package me.rei_m.hbfavkotlin.views.widgets.manager

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import me.rei_m.hbfavkotlin.events.BookmarkPageDisplayEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.views.adapters.BookmarkPagerAdaptor
import me.rei_m.hbfavkotlin.events.BookmarkPageDisplayEvent.Companion.Kind as pageKind

class BookmarkViewPager : ViewPager {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    public fun init(supportFragmentManager: FragmentManager, context: Context) {

        adapter = BookmarkPagerAdaptor(supportFragmentManager, context)

        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                postPageDisplayEvent(position)
            }
        })
    }

    public fun getCurrentPageTitle() = adapter.getPageTitle(currentItem)

    public fun postCurrentPageDisplayEvent() {
        postPageDisplayEvent(currentItem)
    }

    private fun postPageDisplayEvent(position: Int) {
        when (position) {
            BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_FAVORITE ->
                EventBusHolder.EVENT_BUS.post(BookmarkPageDisplayEvent(pageKind.BOOKMARK_FAVORITE))
            BookmarkPagerAdaptor.INDEX_PAGER_BOOKMARK_OWN ->
                EventBusHolder.EVENT_BUS.post(BookmarkPageDisplayEvent(pageKind.BOOKMARK_OWN))
            BookmarkPagerAdaptor.INDEX_PAGER_HOT_ENTRY ->
                EventBusHolder.EVENT_BUS.post(BookmarkPageDisplayEvent(pageKind.HOT_ENTRY))
            BookmarkPagerAdaptor.INDEX_PAGER_NEW_ENTRY ->
                EventBusHolder.EVENT_BUS.post(BookmarkPageDisplayEvent(pageKind.NEW_ENTRY))
        }
    }
}