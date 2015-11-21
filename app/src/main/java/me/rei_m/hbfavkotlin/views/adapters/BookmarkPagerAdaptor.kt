package me.rei_m.hbfavkotlin.views.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import me.rei_m.hbfavkotlin.fragments.FavoriteFragment

class BookmarkPagerAdaptor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        return FavoriteFragment.newInstance()
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "${position + 1} 枚目"
    }
}
