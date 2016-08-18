package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.BookmarkEntity

interface BookmarkFavoriteContact {

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

        fun onCreate(view: BookmarkFavoriteContact.View)

        fun onResume()

        fun onPause()

        fun onRefreshList()

        fun onScrollEnd(nextIndex: Int)

        fun onClickBookmark(bookmarkEntity: BookmarkEntity)
    }
}
