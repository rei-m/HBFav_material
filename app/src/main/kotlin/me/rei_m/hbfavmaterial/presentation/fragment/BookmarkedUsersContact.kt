package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter

interface BookmarkedUsersContact {

    interface View {

        fun showUserList(bookmarkList: List<BookmarkEntity>)

        fun hideUserList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun showEmpty()

        fun hideEmpty()

        fun navigateToOthersBookmark(bookmarkEntity: BookmarkEntity)
    }

    interface Actions {

        var bookmarkCommentFilter: BookmarkCommentFilter

        fun onCreate(view: View,
                     bookmarkEntity: BookmarkEntity,
                     bookmarkCommentFilter: BookmarkCommentFilter)

        fun onResume()

        fun onPause()

        fun onClickUser(bookmarkEntity: BookmarkEntity)

        fun onRefreshList()

        fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter)
    }
}
