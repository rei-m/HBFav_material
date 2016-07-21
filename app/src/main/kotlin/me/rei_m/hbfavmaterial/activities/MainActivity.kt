package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.ViewPager
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.fragments.MainPageFragment
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor
import me.rei_m.hbfavmaterial.views.widgets.manager.BookmarkViewPager

/**
 * メインActivity.
 */
class MainActivity : BaseDrawerActivity() {

    companion object {

        private val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        fun createIntent(context: Context,
                         index: Int = BookmarkPagerAdaptor.Page.BOOKMARK_FAVORITE.index): Intent {
            return Intent(context, MainActivity::class.java)
                    .putExtra(ARG_PAGER_INDEX, index)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        val currentPagerIndex = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdaptor.Page.BOOKMARK_FAVORITE.index)

        supportActionBar?.title = BookmarkPagerAdaptor.Page.values()[currentPagerIndex].title(applicationContext, "")

        with(findViewById(R.id.activity_main_nav) as NavigationView) {
            setCheckedItem(BookmarkPagerAdaptor.Page.values()[currentPagerIndex].navId)
        }

        with(findViewById(R.id.pager) as BookmarkViewPager) {
            initialize(supportFragmentManager)
            currentItem = currentPagerIndex

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    setTitleAndMenu(position)
                }
            })
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Drawer内のメニュー選択時のイベント
        when (item.itemId) {
            R.id.nav_setting ->
                startActivityWithClearTop(SettingActivity.createIntent(this))
            R.id.nav_explain_app ->
                startActivityWithClearTop(ExplainAppActivity.createIntent(this))
            else -> {
                with(findViewById(R.id.pager) as BookmarkViewPager) {
                    currentItem = BookmarkPagerAdaptor.Page.forMenuId(item.itemId).index
                }
            }
        }

        return super.onNavigationItemSelected(item)
    }

    private fun setTitleAndMenu(position: Int) {
        for (fragment in supportFragmentManager.fragments) {
            fragment as MainPageFragment
            if (fragment.pageIndex == position) {
                supportActionBar?.title = fragment.pageTitle
                with(findViewById(R.id.activity_main_nav) as NavigationView) {
                    setCheckedItem(BookmarkPagerAdaptor.Page.values()[position].navId)
                }
                break
            }
        }
    }
}
