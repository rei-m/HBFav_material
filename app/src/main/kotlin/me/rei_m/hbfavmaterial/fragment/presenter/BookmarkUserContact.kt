package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.di.FragmentComponent
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter

interface BookmarkUserContact {

    interface View {

        fun showBookmarkList(bookmarkList: List<BookmarkEntity>)

        fun hideBookmarkList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun startAutoLoading()

        fun stopAutoLoading()

        fun showEmpty()

        fun hideEmpty()
    }

    interface Actions {

        var readAfterFilter: ReadAfterFilter

        fun onCreate(component: FragmentComponent,
                     view: BookmarkUserContact.View,
                     isOwner: Boolean,
                     bookmarkUserId: String)

        fun onResume()

        fun onPause()

        fun onRefreshList()

        fun onScrollEnd(nextIndex: Int)

        fun onOptionItemSelected(readAfterFilter: ReadAfterFilter)

        fun onClickBookmark(bookmarkEntity: BookmarkEntity)
    }
}
