package me.rei_m.hbfavmaterial.fragment.presenter

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

        fun navigateToBookmark(bookmarkEntity: BookmarkEntity)
    }

    interface Actions {

        var readAfterFilter: ReadAfterFilter

        fun onCreate(view: BookmarkUserContact.View,
                     isOwner: Boolean,
                     bookmarkUserId: String,
                     readAfterFilter: ReadAfterFilter)

        fun onResume()

        fun onPause()

        fun onRefreshList()

        fun onScrollEnd(nextIndex: Int)

        fun onOptionItemSelected(readAfterFilter: ReadAfterFilter)

        fun onClickBookmark(bookmarkEntity: BookmarkEntity)
    }
}
