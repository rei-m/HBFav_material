package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.*
import me.rei_m.hbfavmaterial.usecase.*

@Module
open class FragmentModule {

    @Provides
    fun provideBookmarkedUsersPresenter(getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase): BookmarkedUsersContact.Actions {
        return createBookmarkedUsersPresenter(getBookmarkedUsersUsecase)
    }

    open fun createBookmarkedUsersPresenter(getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase): BookmarkedUsersContact.Actions {
        return BookmarkedUsersPresenter(getBookmarkedUsersUsecase)
    }

    @Provides
    fun provideBookmarkFavoritePresenter(getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase): BookmarkFavoriteContact.Actions {
        return createBookmarkFavoritePresenter(getFavoriteBookmarksUsecase)
    }

    open fun createBookmarkFavoritePresenter(getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase): BookmarkFavoriteContact.Actions {
        return BookmarkFavoritePresenter(getFavoriteBookmarksUsecase)
    }

    @Provides
    fun provideBookmarkUserPresenter(getUserBookmarksUsecase: GetUserBookmarksUsecase): BookmarkUserContact.Actions {
        return createBookmarkUserPresenter(getUserBookmarksUsecase)
    }

    open fun createBookmarkUserPresenter(getUserBookmarksUsecase: GetUserBookmarksUsecase): BookmarkUserContact.Actions {
        return BookmarkUserPresenter(getUserBookmarksUsecase)
    }

    @Provides
    fun provideHotEntryPresenter(getHotEntriesUsecase: GetHotEntriesUsecase): HotEntryContact.Actions {
        return createHotEntryPresenter(getHotEntriesUsecase)
    }

    open fun createHotEntryPresenter(getHotEntriesUsecase: GetHotEntriesUsecase): HotEntryContact.Actions {
        return HotEntryPresenter(getHotEntriesUsecase)
    }

    @Provides
    fun provideNewEntryPresenter(getNewEntriesUsecase: GetNewEntriesUsecase): NewEntryContact.Actions {
        return createHotEntryPresenter(getNewEntriesUsecase)
    }

    open fun createHotEntryPresenter(getNewEntriesUsecase: GetNewEntriesUsecase): NewEntryContact.Actions {
        return NewEntryPresenter(getNewEntriesUsecase)
    }

    @Provides
    fun provideInitializePresenter(getUserUsecase: GetUserUsecase,
                                   confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): InitializeContact.Actions {
        return createInitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
    }

    open fun createInitializePresenter(getUserUsecase: GetUserUsecase,
                                       confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): InitializeContact.Actions {
        return InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
    }

    @Provides
    fun provideSettingPresenter(getUserUsecase: GetUserUsecase,
                                getHatenaTokenUsecase: GetHatenaTokenUsecase,
                                getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                authorizeTwitterUsecase: AuthorizeTwitterUsecase): SettingContact.Actions {
        return createSettingPresenter(getUserUsecase, getHatenaTokenUsecase, getTwitterSessionUsecase, authorizeTwitterUsecase)
    }

    open fun createSettingPresenter(getUserUsecase: GetUserUsecase,
                                    getHatenaTokenUsecase: GetHatenaTokenUsecase,
                                    getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                    authorizeTwitterUsecase: AuthorizeTwitterUsecase): SettingContact.Actions {
        return SettingPresenter(getUserUsecase, getHatenaTokenUsecase, getTwitterSessionUsecase, authorizeTwitterUsecase)
    }
}
