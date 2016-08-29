package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.fragment.presenter.*
import me.rei_m.hbfavmaterial.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.EntryService
import me.rei_m.hbfavmaterial.service.TwitterService
import me.rei_m.hbfavmaterial.service.UserService

@Module
open class FragmentModule {

    @Provides
    fun provideBookmarkedUsersPresenter(bookmarkService: BookmarkService): BookmarkedUsersContact.Actions {
        return createBookmarkedUsersPresenter(bookmarkService)
    }

    open fun createBookmarkedUsersPresenter(bookmarkService: BookmarkService): BookmarkedUsersContact.Actions {
        return BookmarkedUsersPresenter(bookmarkService)
    }

    @Provides
    fun provideBookmarkFavoritePresenter(userRepository: UserRepository,
                                         bookmarkService: BookmarkService): BookmarkFavoriteContact.Actions {
        return createBookmarkFavoritePresenter(userRepository, bookmarkService)
    }

    open fun createBookmarkFavoritePresenter(userRepository: UserRepository,
                                             bookmarkService: BookmarkService): BookmarkFavoriteContact.Actions {
        return BookmarkFavoritePresenter(userRepository, bookmarkService)
    }

    @Provides
    fun provideBookmarkUserPresenter(userRepository: UserRepository,
                                     bookmarkService: BookmarkService): BookmarkUserContact.Actions {
        return createBookmarkUserPresenter(userRepository, bookmarkService)
    }

    open fun createBookmarkUserPresenter(userRepository: UserRepository,
                                         bookmarkService: BookmarkService): BookmarkUserContact.Actions {
        return BookmarkUserPresenter(userRepository, bookmarkService)
    }

    @Provides
    fun provideHotEntryPresenter(entryService: EntryService): HotEntryContact.Actions {
        return HotEntryPresenter(entryService)
    }

    @Provides
    fun provideNewEntryPresenter(entryService: EntryService): NewEntryContact.Actions {
        return NewEntryPresenter(entryService)
    }

    @Provides
    fun provideInitializePresenter(userRepository: UserRepository,
                                   userService: UserService): InitializeContact.Actions {
        return createInitializePresenter(userRepository, userService)
    }

    open fun createInitializePresenter(userRepository: UserRepository,
                                       userService: UserService): InitializeContact.Actions {
        return InitializePresenter(userRepository, userService)
    }

    @Provides
    fun provideSettingPresenter(userRepository: UserRepository,
                                hatenaTokenRepository: HatenaTokenRepository,
                                twitterSessionRepository: TwitterSessionRepository,
                                twitterService: TwitterService): SettingContact.Actions {
        return SettingPresenter(userRepository, hatenaTokenRepository, twitterSessionRepository, twitterService)
    }
}
