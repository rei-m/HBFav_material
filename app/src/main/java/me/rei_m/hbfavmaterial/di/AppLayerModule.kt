package me.rei_m.hbfavmaterial.di

import android.app.Application
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.models.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppLayerModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideBookmarkFavoriteModel(): BookmarkFavoriteModel {
        return BookmarkFavoriteModel()
    }

    @Provides
    @Singleton
    @Named("bookmarkUserModelForSelf")
    fun provideBookmarkUserModelForSelf(): BookmarkUserModel {
        return BookmarkUserModel()
    }

    @Provides
    @Singleton
    @Named("bookmarkUserModelForOther")
    fun provideBookmarkUserModelForOther(): BookmarkUserModel {
        return BookmarkUserModel()
    }

    @Provides
    @Singleton
    fun provideHatenaModel(): HatenaModel {
        return HatenaModel(application)
    }

    @Provides
    @Singleton
    fun provideHotEntryModel(): HotEntryModel {
        return HotEntryModel()
    }

    @Provides
    @Singleton
    fun provideNewEntryModel(): NewEntryModel {
        return NewEntryModel()
    }

    @Provides
    @Singleton
    fun provideUserModel(): UserModel {
        return UserModel(application)
    }

    @Provides
    @Singleton
    fun provideUserRegisterBookmarkModel(): UserRegisterBookmarkModel {
        return UserRegisterBookmarkModel()
    }
}
