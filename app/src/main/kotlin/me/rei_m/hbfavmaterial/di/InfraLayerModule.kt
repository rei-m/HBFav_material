package me.rei_m.hbfavmaterial.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.application.impl.HatenaServiceImpl
import me.rei_m.hbfavmaterial.application.impl.TwitterServiceImpl
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import javax.inject.Singleton

@Module
open class InfraLayerModule {

    @Provides
    @Singleton
    fun provideHatenaOAuthManager(context: Context): HatenaOAuthManager {
        return HatenaOAuthManager(context.getString(R.string.api_key_hatena_consumer_key),
                context.getString(R.string.api_key_hatena_consumer_secret))
    }

    @Provides
    @Singleton
    fun provideTwitterService(context: Context): TwitterService {
        return TwitterServiceImpl(context.getAppPreferences("TwitterModel"))
    }

    @Provides
    @Singleton
    fun provideHatenaService(context: Context,
                             hatenaOAuthManager: HatenaOAuthManager): HatenaService {
        return HatenaServiceImpl(context.getAppPreferences("HatenaModel"), hatenaOAuthManager)
    }

    @Provides
    @Singleton
    fun provideUserModel(context: Context): UserModel {
        val hatenaApiService = RetrofitManager.scalar.create(HatenaApiService::class.java)
        return UserModel(context.getAppPreferences("UserModel"), hatenaApiService)
    }

    /**
     * アプリ内で使用するSharedPreferenceを取得する.
     */
    private fun Context.getAppPreferences(key: String): SharedPreferences {
        return getSharedPreferences(key, Context.MODE_PRIVATE)
    }
}
