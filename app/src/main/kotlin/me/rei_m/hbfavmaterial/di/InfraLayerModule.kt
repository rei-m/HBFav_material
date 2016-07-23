package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.repositories.BookmarkRepository
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository

@Module
class InfraLayerModule() {

    @Provides
    fun provideBookmarkRepository(): BookmarkRepository {
        return BookmarkRepository()
    }

    @Provides
    @ForApplication
    fun provideHatenaRepository(context: Context): HatenaRepository {
        return HatenaRepository(context)
    }

    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepository()
    }
}
