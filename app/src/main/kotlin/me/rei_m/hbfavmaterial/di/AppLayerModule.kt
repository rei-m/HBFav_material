package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.models.HatenaModel
import me.rei_m.hbfavmaterial.models.TwitterModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.models.UserRegisterBookmarkModel
import me.rei_m.hbfavmaterial.repositories.BookmarkRepository
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository
import javax.inject.Singleton

@Module
class AppLayerModule() {

    @Provides
    @Singleton
    @ForApplication
    fun provideHatenaModel(context: Context, hatenaRepository: HatenaRepository): HatenaModel {
        return HatenaModel(context, hatenaRepository)
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
