/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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

        private const val KEY_PAGER_INDEX = "KEY_PAGER_INDEX"

        fun createIntent(context: Context,
                         page: BookmarkPagerAdapter.Page): Intent {
            return Intent(context, MainActivity::class.java)
                    .putExtra(ARG_PAGER_INDEX, page.index)
        }
    }

    private val initialPagerIndex by lazy {
        intent.getIntExtra(ARG_PAGER_INDEX, BookmarkPagerAdapter.Page.BOOKMARK_FAVORITE.index)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentPage = if (savedInstanceState == null) {
            BookmarkPagerAdapter.Page.values()[initialPagerIndex]
        } else {
            BookmarkPagerAdapter.Page.values()[savedInstanceState.getInt(KEY_PAGER_INDEX)]
        }

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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        binding?.appBar?.pager?.currentItem?.let {
            outState?.putInt(KEY_PAGER_INDEX, it)
        } ?: let {
            outState?.putInt(KEY_PAGER_INDEX, initialPagerIndex)
        }
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

    override fun onUpdateFilter(pageTitle: String) {
        supportActionBar?.title = pageTitle
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
