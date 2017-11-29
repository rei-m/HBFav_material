package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MenuItem
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.fragment.*
import me.rei_m.hbfavmaterial.presentation.widget.viewpager.BookmarkViewPager
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel
import me.rei_m.hbfavmaterial.viewmodel.activity.di.BaseDrawerActivityViewModelModule

/**
 * メインActivity.
 */
class MainActivity : BaseDrawerActivity(),
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
                        if (fragment is MainPageFragment) {
                            if (fragment.pageIndex == position) {
                                supportActionBar?.title = fragment.pageTitle
                                break
                            }
                        }
                    }
                }
            })
        }

        viewModel.onNavigationPageSelected(currentPage)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setting -> {
                onNavigationSettingSelected()
            }
            R.id.nav_explain_app -> {
                onNavigationExplainAppSelected()
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

    override fun provideViewModel(): BaseDrawerActivityViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BaseDrawerActivityViewModel::class.java)

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            BaseDrawerActivityViewModelModule::class,
            FavoriteBookmarkFragment.Module::class,
            UserBookmarkFragment.Module::class,
            HotEntryFragment.Module::class,
            NewEntryFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<MainActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<MainActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: MainActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(MainActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
