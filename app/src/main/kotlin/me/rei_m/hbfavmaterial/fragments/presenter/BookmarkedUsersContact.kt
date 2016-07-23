package me.rei_m.hbfavmaterial.fragments.presenter

import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.BookmarkCommentFilter
import rx.Subscription

interface BookmarkedUsersContact {

    interface View {

        fun showUserList(bookmarkList: List<BookmarkEntity>)

        fun hideUserList()

        fun showNetworkErrorMessage()

        fun showProgress()

        fun hideProgress()

        fun showEmpty()

        fun hideEmpty()
    }

    interface Actions {

        fun clickUser(bookmarkEntity: BookmarkEntity)

        fun initializeListContents(): Subscription?

        fun fetchListContents(): Subscription?

        fun toggleListContents(bookmarkCommentFilter: BookmarkCommentFilter)
    }
}
