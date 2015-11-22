package me.rei_m.hbfavkotlin.views.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import me.rei_m.hbfavkotlin.fragments.BookmarkFavoriteFragment
import me.rei_m.hbfavkotlin.fragments.BookmarkOwnFragment
import me.rei_m.hbfavkotlin.fragments.HotEntryFragment

class BookmarkPagerAdaptor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> BookmarkFavoriteFragment.newInstance()
            1 -> BookmarkOwnFragment.newInstance()
            2 -> HotEntryFragment.newInstance()
            3 -> BookmarkFavoriteFragment.newInstance()
            else -> null
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "${position + 1} 枚目"
    }
}
