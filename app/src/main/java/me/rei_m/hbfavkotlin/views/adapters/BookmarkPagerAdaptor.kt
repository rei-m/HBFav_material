package me.rei_m.hbfavkotlin.views.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.fragments.BookmarkFavoriteFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkUserFragment
import me.rei_m.hbfavkotlin.fragments.HotEntryFragment
import me.rei_m.hbfavkotlin.fragments.NewEntryFragment
import java.util.*

class BookmarkPagerAdaptor : FragmentStatePagerAdapter {

    private val mTitleList = ArrayList<String>()

    constructor(fm: FragmentManager, context: Context) : super(fm) {
        mTitleList.add(INDEX_PAGER_BOOKMARK_FAVORITE,
                context.getString(R.string.fragment_title_bookmark_favorite))
        mTitleList.add(INDEX_PAGER_BOOKMARK_OWN,
                context.getString(R.string.fragment_title_bookmark_own))
        mTitleList.add(INDEX_PAGER_HOT_ENTRY,
                context.getString(R.string.fragment_title_hot_entry))
        mTitleList.add(INDEX_PAGER_NEW_ENTRY,
                context.getString(R.string.fragment_title_new_entry))
    }

    companion object {
        private const final val PAGE_COUNT = 4
        public const final val INDEX_PAGER_BOOKMARK_FAVORITE = 0
        public const final val INDEX_PAGER_BOOKMARK_OWN = 1
        public const final val INDEX_PAGER_HOT_ENTRY = 2
        public const final val INDEX_PAGER_NEW_ENTRY = 3
    }

    override fun getItem(position: Int): Fragment? {

        return when (position) {
            INDEX_PAGER_BOOKMARK_FAVORITE ->
                BookmarkFavoriteFragment.newInstance()
            INDEX_PAGER_BOOKMARK_OWN ->
                BookmarkUserFragment.newInstance()
            INDEX_PAGER_HOT_ENTRY ->
                HotEntryFragment.newInstance()
            INDEX_PAGER_NEW_ENTRY ->
                NewEntryFragment.newInstance()
            else ->
                null
        }
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleList[position]
    }
}
