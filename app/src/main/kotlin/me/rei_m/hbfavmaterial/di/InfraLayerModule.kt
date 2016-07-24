package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository
import me.rei_m.hbfavmaterial.repositories.impl.HatenaTokenRepositoryImpl
import me.rei_m.hbfavmaterial.repositories.impl.UserRepositoryImpl
import javax.inject.Singleton

@Module
class InfraLayerModule() {

    @Provides
    @Singleton
    fun provideHatenaOAuthManager(@ForApplication context: Context): HatenaOAuthManager {
        return HatenaOAuthManager(
                context.getString(R.string.api_key_hatena_consumer_key),
                context.getString(R.string.api_key_hatena_consumer_secret)
        )
    }
    
    @Provides
    fun provideHatenaRepository(@ForApplication context: Context): HatenaRepository {
        return HatenaRepository(context)
    }

    @Provides
    @Singleton
    fun provideHatenaTokenRepository(@ForApplication context: Context): HatenaTokenRepository {
        return HatenaTokenRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ForApplication context: Context): UserRepository {
        return UserRepositoryImpl(context)
    }
}
