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

package me.rei_m.hbfavmaterial.presentation.widget.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.Menu
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.presentation.widget.fragment.FavoriteBookmarkFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.HotEntryFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.NewEntryFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.UserBookmarkFragment

/**
 * メインページのフラグメントを管理するAdaptor.
 */
class BookmarkPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    enum class Page(val navId: Int,
                    val titleResId: Int) {

        BOOKMARK_FAVORITE(R.id.nav_bookmark_favorite, R.string.fragment_title_bookmark_favorite) {
            override fun newInstance(): Fragment = FavoriteBookmarkFragment.newInstance(ordinal)

            override fun toggleMenu(menu: Menu) {
                menu.hide()
            }
        },
        BOOKMARK_OWN(R.id.nav_bookmark_own, R.string.fragment_title_bookmark_own) {
            override fun newInstance(): Fragment = UserBookmarkFragment.newInstance(ordinal)

            override fun toggleMenu(menu: Menu) {
                with(menu) {
                    hide()
                    findItem(R.id.menu_filter_bookmark_all).isVisible = true
                    findItem(R.id.menu_filter_bookmark_read_after).isVisible = true
                }
            }
        },
        HOT_ENTRY(R.id.nav_hot_entry, R.string.fragment_title_hot_entry) {
            override fun newInstance(): Fragment = HotEntryFragment.newInstance(ordinal)

            override fun toggleMenu(menu: Menu) {
                with(menu) {
                    show()
                    findItem(R.id.menu_filter_bookmark_all).isVisible = false
                    findItem(R.id.menu_filter_bookmark_read_after).isVisible = false
                }
            }
        },
        NEW_ENTRY(R.id.nav_new_entry, R.string.fragment_title_new_entry) {
            override fun newInstance(): Fragment = NewEntryFragment.newInstance(ordinal)

            override fun toggleMenu(menu: Menu) {
                with(menu) {
                    show()
                    findItem(R.id.menu_filter_bookmark_all).isVisible = false
                    findItem(R.id.menu_filter_bookmark_read_after).isVisible = false
                }
            }
        };

        companion object {
            fun forMenuId(menuId: Int): Page {
                values().filter { it.navId == menuId }.forEach { return it }
                throw AssertionError("no menu enum found for the id. you forgot to implement?")
            }
        }

        val index: Int = this.ordinal

        fun title(context: Context, subTitle: String) = context.getString(titleResId) + if (subTitle.isNotEmpty()) " - $subTitle" else ""

        abstract fun toggleMenu(menu: Menu)

        abstract fun newInstance(): Fragment
    }

    override fun getItem(position: Int): Fragment? {
        return Page.values()[position].newInstance()
    }

    override fun getCount(): Int {
        return Page.values().count()
    }
}
