package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.models.*
import me.rei_m.hbfavmaterial.repositories.BookmarkRepository
import me.rei_m.hbfavmaterial.repositories.EntryRepository
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppLayerModule() {

    @Provides
    @Singleton
    fun provideBookmarkFavoriteModel(bookmarkRepository: BookmarkRepository): BookmarkFavoriteModel {
        return BookmarkFavoriteModel(bookmarkRepository)
    }

    @Provides
    @Singleton
    @Named("bookmarkUserModelForSelf")
    fun provideBookmarkUserModelForSelf(bookmarkRepository: BookmarkRepository): BookmarkUserModel {
        return BookmarkUserModel(bookmarkRepository)
    }

    @Provides
    @Singleton
    @Named("bookmarkUserModelForOther")
    fun provideBookmarkUserModelForOther(bookmarkRepository: BookmarkRepository): BookmarkUserModel {
        return BookmarkUserModel(bookmarkRepository)
    }

    @Provides
    @Singleton
    @ForApplication
    fun provideHatenaModel(context: Context, hatenaRepository: HatenaRepository): HatenaModel {
        return HatenaModel(context, hatenaRepository)
    }

    @Provides
    @Singleton
    fun provideHotEntryModel(entryRepository: EntryRepository): HotEntryModel {
        return HotEntryModel(entryRepository)
    }

    @Provides
    @Singleton
    fun provideNewEntryModel(entryRepository: EntryRepository): NewEntryModel {
        return NewEntryModel(entryRepository)
    }

    @Provides
    @Singleton
    @ForApplication
    fun provideTwitterModel(context: Context): TwitterModel {
        return TwitterModel(context)
    }

    @Provides
    @Singleton
    @ForApplication
    fun provideUserModel(context: Context, userRepository: UserRepository): UserModel {
        return UserModel(context, userRepository)
    }

    @Provides
    @Singleton
    fun provideUserRegisterBookmarkModel(bookmarkRepository: BookmarkRepository): UserRegisterBookmarkModel {
        return UserRegisterBookmarkModel(bookmarkRepository)
    }
}
