package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogContact
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.fragment.presenter.EditUserIdDialogContact
import me.rei_m.hbfavmaterial.fragment.presenter.EditUserIdDialogPresenter
import me.rei_m.hbfavmaterial.network.HatenaApiService
import me.rei_m.hbfavmaterial.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.network.RetrofitManager
import me.rei_m.hbfavmaterial.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.*
import me.rei_m.hbfavmaterial.service.impl.*

@Module
class ActivityModule() {

    @Provides
    fun provideUserService(): UserService {
        return UserServiceImpl(RetrofitManager.scalar.create(HatenaApiService::class.java))
    }

    @Provides
    fun provideBookmarkService(): BookmarkService {
        return BookmarkServiceImpl()
    }

    @Provides
    fun provideEntryService(): EntryService {
        return EntryServiceImpl()
    }

    @Provides
    fun provideHatenaService(hatenaOAuthManager: HatenaOAuthManager): HatenaService {
        return HatenaServiceImpl(hatenaOAuthManager)
    }

    @Provides
    fun provideTwitterService(twitterSessionRepository: TwitterSessionRepository): TwitterService {
        return TwitterServiceImpl(twitterSessionRepository)
    }

    @Provides
    fun provideEditBookmarkDialogPresenter(userRepository: UserRepository,
                                           hatenaTokenRepository: HatenaTokenRepository,
                                           hatenaService: HatenaService,
                                           twitterSessionRepository: TwitterSessionRepository,
                                           twitterService: TwitterService): EditBookmarkDialogContact.Actions {
        return EditBookmarkDialogPresenter(userRepository,
                hatenaTokenRepository,
                hatenaService,
                twitterSessionRepository,
                twitterService)
    }

    @Provides
    fun provideEditUserIdDialogPresenter(userRepository: UserRepository,
                                         userService: UserService): EditUserIdDialogContact.Actions {
        return EditUserIdDialogPresenter(userRepository, userService)
    }
}
