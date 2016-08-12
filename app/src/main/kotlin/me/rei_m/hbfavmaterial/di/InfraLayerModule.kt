package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.repository.impl.HatenaTokenRepositoryImpl
import me.rei_m.hbfavmaterial.repository.impl.TwitterSessionRepositoryImpl
import me.rei_m.hbfavmaterial.repository.impl.UserRepositoryImpl
import javax.inject.Singleton

@Module
open class InfraLayerModule() {

    @Provides
    @Singleton
    fun provideHatenaOAuthManager(@ForApplication context: Context): HatenaOAuthManager {
        return HatenaOAuthManager(
                context.getString(R.string.api_key_hatena_consumer_key),
                context.getString(R.string.api_key_hatena_consumer_secret)
        )
    }

    @Provides
    @Singleton
    fun provideHatenaTokenRepository(@ForApplication context: Context): HatenaTokenRepository {
        return HatenaTokenRepositoryImpl(context)
    }

    @Provides
    @Singleton
    open fun provideUserRepository(@ForApplication context: Context): UserRepository {
        return UserRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideTwitterSessionRepository(@ForApplication context: Context): TwitterSessionRepository {
        return TwitterSessionRepositoryImpl(context)
    }
}
