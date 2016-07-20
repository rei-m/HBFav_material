package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.Menu
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.fragments.BookmarkFavoriteFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUserFragment
import me.rei_m.hbfavmaterial.fragments.HotEntryFragment
import me.rei_m.hbfavmaterial.fragments.NewEntryFragment

/**
 * メインページのフラグメントを管理するAdaptor.
 */
class BookmarkPagerAdaptor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    enum class Page(val navId: Int,
                    val titleResId: Int) {

        BOOKMARK_FAVORITE(R.id.nav_bookmark_favorite, R.string.fragment_title_bookmark_favorite) {
            override fun newInstance(): Fragment = BookmarkFavoriteFragment.newInstance(ordinal)

            override fun toggleMenu(menu: Menu) {
                menu.hide()
            }
        },
        BOOKMARK_OWN(R.id.nav_bookmark_own, R.string.fragment_title_bookmark_own) {
            override fun newInstance(): Fragment = BookmarkUserFragment.newInstance(ordinal)

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
                for (page: Page in values()) {
                    if (page.navId == menuId) {
                        return page
                    }
                }
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

    fun getPageTitle(fm: FragmentManager, position: Int): CharSequence {
        for (f in fm.fragments) {
            when (f) {
                is BookmarkFavoriteFragment -> {

                }

                is BookmarkUserFragment -> {

                }

                is HotEntryFragment -> {

                }

                is NewEntryFragment -> {

                }
                else -> {

                }
            }
        }
        return super.getPageTitle(position)
    }
}
