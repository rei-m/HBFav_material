package me.rei_m.hbfavmaterial.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.EntryService
import me.rei_m.hbfavmaterial.service.HatenaService
import me.rei_m.hbfavmaterial.service.UserService
import me.rei_m.hbfavmaterial.service.impl.BookmarkServiceImpl
import me.rei_m.hbfavmaterial.service.impl.EntryServiceImpl
import me.rei_m.hbfavmaterial.service.impl.HatenaServiceImpl
import me.rei_m.hbfavmaterial.service.impl.UserServiceImpl

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
}
