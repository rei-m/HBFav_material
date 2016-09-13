package me.rei_m.hbfavmaterial.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.repository.*
import me.rei_m.hbfavmaterial.domain.repository.impl.*
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.domain.service.TwitterService
import me.rei_m.hbfavmaterial.domain.service.impl.HatenaServiceImpl
import me.rei_m.hbfavmaterial.domain.service.impl.TwitterServiceImpl
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
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
        return HatenaTokenRepositoryImpl(context.getAppPreferences("HatenaModel"))
    }

    @Provides
    @Singleton
    fun provideHatenaAccountRepository(): HatenaAccountRepository {
        val hatenaApiService = RetrofitManager.scalar.create(HatenaApiService::class.java)
        return HatenaAccountRepositoryImpl(hatenaApiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ForApplication context: Context): UserRepository {
        // UserModelから移行したのでキーはそのまま.
        return UserRepositoryImpl(context.getAppPreferences("UserModel"))
    }

    @Provides
    @Singleton
    fun provideTwitterSessionRepository(@ForApplication context: Context): TwitterSessionRepository {
        return TwitterSessionRepositoryImpl(context.getAppPreferences("TwitterModel"))
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(): BookmarkRepository {
        val hatenaRssService = RetrofitManager.xml.create(HatenaRssService::class.java)
        val hatenaApiService = RetrofitManager.json.create(HatenaApiService::class.java)
        return BookmarkRepositoryImpl(hatenaRssService, hatenaApiService)
    }

    @Provides
    @Singleton
    fun provideEntryRepository(): EntryRepository {
        val hatenaRssService = RetrofitManager.xml.create(HatenaRssService::class.java)
        val hotEntryRssService = RetrofitManager.xmlForHotEntryAll.create(HatenaRssService::class.java)
        return EntryRepositoryImpl(hatenaRssService, hotEntryRssService)
    }

    @Provides
    @Singleton
    fun provideTwitterService(twitterSessionRepository: TwitterSessionRepository): TwitterService {
        return TwitterServiceImpl(twitterSessionRepository)
    }

    @Provides
    @Singleton
    fun provideHatenaService(hatenaOAuthManager: HatenaOAuthManager): HatenaService {
        return HatenaServiceImpl(hatenaOAuthManager)
    }

    /**
     * アプリ内で使用するSharedPreferenceを取得する.
     */
    private fun Context.getAppPreferences(key: String): SharedPreferences {
        return getSharedPreferences(key, Context.MODE_PRIVATE)
    }
}
