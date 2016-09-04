package me.rei_m.hbfavmaterial.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.*
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.domain.service.TwitterService
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.*
import me.rei_m.hbfavmaterial.usecase.impl.*
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    /**
     * Allow the application context to be injected
     * but require that it be annotated with [ ][ForApplication] to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideActivityNavigator(): ActivityNavigator {
        return ActivityNavigator()
    }

    @Provides
    fun provideGetBookmarkedUsersUsecase(bookmarkRepository: BookmarkRepository): GetBookmarkedUsersUsecase {
        return GetBookmarkedUsersUsecaseImpl(bookmarkRepository)
    }

    @Provides
    fun provideGetFavoriteBookmarksUsecase(bookmarkRepository: BookmarkRepository, userRepository: UserRepository): GetFavoriteBookmarksUsecase {
        return GetFavoriteBookmarksUsecaseImpl(bookmarkRepository, userRepository)
    }

    @Provides
    fun provideGetUserBookmarksUsecase(bookmarkRepository: BookmarkRepository, userRepository: UserRepository): GetUserBookmarksUsecase {
        return GetUserBookmarksUsecaseImpl(bookmarkRepository, userRepository)
    }

    @Provides
    fun provideRegisterBookmarkUsecase(hatenaTokenRepository: HatenaTokenRepository,
                                       hatenaService: HatenaService,
                                       twitterService: TwitterService): RegisterBookmarkUsecase {
        return RegisterBookmarkUsecaseImpl(hatenaTokenRepository, hatenaService, twitterService)
    }

    @Provides
    fun provideDeleteBookmarkUsecase(hatenaTokenRepository: HatenaTokenRepository,
                                     hatenaService: HatenaService): DeleteBookmarkUsecase {
        return DeleteBookmarkUsecaseImpl(hatenaTokenRepository, hatenaService)
    }

    @Provides
    fun provideGetUserUsecase(userRepository: UserRepository): GetUserUsecase {
        return GetUserUsecaseImpl(userRepository)
    }

    @Provides
    fun provideUpdateUserUsecase(userRepository: UserRepository): UpdateUserUsecase {
        return UpdateUserUsecaseImpl(userRepository)
    }

    @Provides
    fun provideGetTwitterSessionUsecase(twitterSessionRepository: TwitterSessionRepository): GetTwitterSessionUsecase {
        return GetTwitterSessionUsecaseImpl(twitterSessionRepository)
    }

    @Provides
    fun provideUpdateTwitterSessionUsecase(twitterSessionRepository: TwitterSessionRepository): UpdateTwitterSessionUsecase {
        return UpdateTwitterSessionUsecaseImpl(twitterSessionRepository)
    }

    @Provides
    fun provideConfirmExistingUserIdUsecase(hatenaAccountRepository: HatenaAccountRepository,
                                            userRepository: UserRepository): ConfirmExistingUserIdUsecase {
        return ConfirmExistingUserIdUsecaseImpl(hatenaAccountRepository, userRepository)
    }

    @Provides
    fun provideGetHotEntriesUsecase(entryRepository: EntryRepository): GetHotEntriesUsecase {
        return GetHotEntriesUsecaseImpl(entryRepository)
    }

    @Provides
    fun provideGetNewEntriesUsecase(entryRepository: EntryRepository): GetNewEntriesUsecase {
        return GetNewEntriesUsecaseImpl(entryRepository)
    }

    @Provides
    fun provideGetHatenaTokenUsecase(hatenaTokenRepository: HatenaTokenRepository): GetHatenaTokenUsecase {
        return GetHatenaTokenUsecaseImpl(hatenaTokenRepository)
    }

    @Provides
    fun provideAuthorizeTwitterUsecase(twitterService: TwitterService): AuthorizeTwitterUsecase {
        return AuthorizeTwitterUsecaseImpl(twitterService)
    }

    @Provides
    fun provideGetBookmarkEditUsecase(hatenaTokenRepository: HatenaTokenRepository,
                                      hatenaService: HatenaService): GetBookmarkEditUsecase {
        return GetBookmarkEditUsecaseImpl(hatenaTokenRepository, hatenaService)
    }

    @Provides
    fun provideAuthorizeHatenaUsecase(hatenaTokenRepository: HatenaTokenRepository,
                                      hatenaService: HatenaService): AuthorizeHatenaUsecase {
        return AuthorizeHatenaUsecaseImpl(hatenaTokenRepository, hatenaService)
    }

    @Provides
    fun provideUnAuthorizeHatenaUsecase(hatenaTokenRepository: HatenaTokenRepository): UnAuthorizeHatenaUsecase {
        return UnAuthorizeHatenaUsecaseImpl(hatenaTokenRepository)
    }
}
