package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.fragments.BookmarkFavoriteFragment
import me.rei_m.hbfavmaterial.fragments.BookmarkUserFragment
import me.rei_m.hbfavmaterial.fragments.HotEntryFragment
import me.rei_m.hbfavmaterial.fragments.NewEntryFragment
import java.util.*

/**
 * メインページのフラグメントを管理するAdaptor.
 */
class BookmarkPagerAdaptor : FragmentStatePagerAdapter {

    private val mTitleList = ArrayList<String>()

    constructor(fm: FragmentManager, context: Context) : super(fm) {
        mTitleList.apply {
            add(INDEX_PAGER_BOOKMARK_FAVORITE, context.getString(R.string.fragment_title_bookmark_favorite))
            add(INDEX_PAGER_BOOKMARK_OWN, context.getString(R.string.fragment_title_bookmark_own))
            add(INDEX_PAGER_HOT_ENTRY, context.getString(R.string.fragment_title_hot_entry))
            add(INDEX_PAGER_NEW_ENTRY, context.getString(R.string.fragment_title_new_entry))
        }
    }

    companion object {
        private val PAGE_COUNT = 4
        val INDEX_PAGER_BOOKMARK_FAVORITE = 0
        val INDEX_PAGER_BOOKMARK_OWN = 1
        val INDEX_PAGER_HOT_ENTRY = 2
        val INDEX_PAGER_NEW_ENTRY = 3
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            INDEX_PAGER_BOOKMARK_FAVORITE -> BookmarkFavoriteFragment.newInstance()
            INDEX_PAGER_BOOKMARK_OWN -> BookmarkUserFragment.newInstance()
            INDEX_PAGER_HOT_ENTRY -> HotEntryFragment.newInstance()
            INDEX_PAGER_NEW_ENTRY -> NewEntryFragment.newInstance()
            else -> BookmarkFavoriteFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleList[position]
    }
}
