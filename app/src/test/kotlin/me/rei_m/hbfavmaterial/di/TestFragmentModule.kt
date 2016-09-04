package me.rei_m.hbfavmaterial.di

import dagger.Module
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkFavoriteContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkUserContact
import me.rei_m.hbfavmaterial.presentation.fragment.BookmarkedUsersContact
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeContact
import me.rei_m.hbfavmaterial.usecase.*

@Module
class TestFragmentModule : FragmentModule() {

    override fun createInitializePresenter(getUserUsecase: GetUserUsecase, confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): InitializeContact.Actions {
        return object : InitializeContact.Actions {
            override fun onCreate(view: InitializeContact.View) {
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onClickButtonSetId(userId: String) {
            }
        }
    }

    override fun createBookmarkedUsersPresenter(getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase): BookmarkedUsersContact.Actions {
        return object : BookmarkedUsersContact.Actions {
            override var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL

            override fun onCreate(view: BookmarkedUsersContact.View,
                                  bookmarkEntity: BookmarkEntity,
                                  bookmarkCommentFilter: BookmarkCommentFilter) {
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onClickUser(bookmarkEntity: BookmarkEntity) {
            }

            override fun onRefreshList() {
            }

            override fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {
            }
        }
    }

    override fun createBookmarkFavoritePresenter(getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase): BookmarkFavoriteContact.Actions {
        return object : BookmarkFavoriteContact.Actions {
            override fun onCreate(view: BookmarkFavoriteContact.View) {
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onRefreshList() {
            }

            override fun onScrollEnd(nextIndex: Int) {
            }

            override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
            }
        }
    }

    override fun createBookmarkUserPresenter(getUserBookmarksUsecase: GetUserBookmarksUsecase): BookmarkUserContact.Actions {
        return object : BookmarkUserContact.Actions {

            override var readAfterFilter: ReadAfterFilter = ReadAfterFilter.ALL

            override fun onCreate(view: BookmarkUserContact.View, isOwner: Boolean, bookmarkUserId: String, readAfterFilter: ReadAfterFilter) {
                this.readAfterFilter = readAfterFilter
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onRefreshList() {
            }

            override fun onScrollEnd(nextIndex: Int) {
            }

            override fun onOptionItemSelected(readAfterFilter: ReadAfterFilter) {
            }

            override fun onClickBookmark(bookmarkEntity: BookmarkEntity) {
            }
        }
    }
}
