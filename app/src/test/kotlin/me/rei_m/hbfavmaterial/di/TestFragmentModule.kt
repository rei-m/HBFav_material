package me.rei_m.hbfavmaterial.di

import dagger.Module
import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.presentation.fragment.*
import me.rei_m.hbfavmaterial.usecase.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@Module
class TestFragmentModule : FragmentModule() {

    override fun createInitializePresenter(getUserUsecase: GetUserUsecase,
                                           confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): InitializeContact.Actions = mock(InitializeContact.Actions::class.java)

    override fun createBookmarkedUsersPresenter(getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase): BookmarkedUsersContact.Actions {
        val presenter = mock(BookmarkedUsersContact.Actions::class.java)
        `when`(presenter.bookmarkCommentFilter).thenReturn(BookmarkCommentFilter.ALL)
        return presenter
    }

    override fun createBookmarkFavoritePresenter(getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase): BookmarkFavoriteContact.Actions = mock(BookmarkFavoriteContact.Actions::class.java)

    override fun createBookmarkUserPresenter(getUserBookmarksUsecase: GetUserBookmarksUsecase): BookmarkUserContact.Actions {
        val presenter = mock(BookmarkUserContact.Actions::class.java)
        `when`(presenter.readAfterFilter).thenReturn(ReadAfterFilter.ALL)
        return presenter
    }

    override fun createHotEntryPresenter(getHotEntriesUsecase: GetHotEntriesUsecase): HotEntryContact.Actions {
        return mock(HotEntryContact.Actions::class.java)
    }

    override fun createHotEntryPresenter(getNewEntriesUsecase: GetNewEntriesUsecase): NewEntryContact.Actions {
        return mock(NewEntryContact.Actions::class.java)
    }

    override fun createSettingPresenter(getUserUsecase: GetUserUsecase,
                                        getHatenaTokenUsecase: GetHatenaTokenUsecase,
                                        getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                        authorizeTwitterUsecase: AuthorizeTwitterUsecase): SettingContact.Actions {
        return mock(SettingContact.Actions::class.java)
    }
}
