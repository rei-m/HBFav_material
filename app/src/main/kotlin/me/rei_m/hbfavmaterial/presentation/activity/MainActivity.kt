package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MenuItem
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.MainActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.MainActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryFragment
import me.rei_m.hbfavmaterial.presentation.fragment.MainPageFragment
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment
import me.rei_m.hbfavmaterial.presentation.fragment.UserBookmarkFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.viewpager.BookmarkViewPager

/**
 * メインActivity.
 */
class MainActivity : BaseDrawerActivity(),
        HasComponent<MainActivityComponent>,
        UserBookmarkFragment.OnFragmentInteractionListener,
        HotEntryFragment.OnFragmentInteractionListener,
        NewEntryFragment.OnFragmentInteractionListener {

    companion object {

        private const val ARG_PAGER_INDEX = "ARG_PAGER_INDEX"

        fun createIntent(context: Context,
                         page: BookmarkPagerAdapter.Page): Intent {
            return Intent(context, MainActivity::class.java)
                    .putExtra(ARG_PAGER_INDEX, page.index)
        }
    }

    private var component: MainActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentPagerIndex = intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdapter.Page.BOOKMARK_FAVORITE.index)

        val currentPage = BookmarkPagerAdapter.Page.values()[currentPagerIndex]

        supportActionBar?.title = currentPage.title(applicationContext, "")

        binding?.appBar?.pager.let {
            it as BookmarkViewPager
            it.initialize(supportFragmentManager)
            it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    if (supportFragmentManager.fragments == null) {
                        return
                    }

                    for (fragment in supportFragmentManager.fragments) {
                        fragment as MainPageFragment
                        if (fragment.pageIndex == position) {
                            supportActionBar?.title = fragment.pageTitle
                            break
                        }
                    }
                }
            })
        }

        viewModel.onNavigationPageSelected(currentPage)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting -> {
                viewModel.onNavigationSettingSelected()
            }
            R.id.nav_explain_app -> {
                viewModel.onNavigationExplainAppSelected()
            }
            else -> {
                viewModel.onNavigationPageSelected(BookmarkPagerAdapter.Page.forMenuId(item.itemId))
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun onUpdateFilter(pageIndex: Int) {
        if (pageIndex == binding?.appBar?.pager?.currentItem) {
            val fragment = supportFragmentManager.fragments[pageIndex] as MainPageFragment
            supportActionBar?.title = fragment.pageTitle
        }
    }

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): MainActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@MainActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): MainActivityComponent {
        val component = (application as App).component
                .plus(MainActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }
}
