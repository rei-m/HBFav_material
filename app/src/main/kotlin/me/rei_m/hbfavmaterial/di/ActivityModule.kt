package me.rei_m.hbfavmaterial.di

import android.content.Context
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogContact
import me.rei_m.hbfavmaterial.fragment.presenter.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.fragment.presenter.EditUserIdDialogContact
import me.rei_m.hbfavmaterial.fragment.presenter.EditUserIdDialogPresenter
import me.rei_m.hbfavmaterial.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.service.*
import me.rei_m.hbfavmaterial.service.impl.*

@Module
class ActivityModule(val activity: AppCompatActivity) {

    @Provides
    fun provideUserService(): UserService {
        return UserServiceImpl()
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
    fun provideEditBookmarkDialogPresenter(@ForApplication context: Context): EditBookmarkDialogContact.Actions {
        return EditBookmarkDialogPresenter(context)
    }

    @Provides
    fun provideEditUserIdDialogPresenter(@ForApplication context: Context): EditUserIdDialogContact.Actions {
        return EditUserIdDialogPresenter(context)
    }
}
