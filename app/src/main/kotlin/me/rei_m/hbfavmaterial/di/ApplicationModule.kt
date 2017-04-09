package me.rei_m.hbfavmaterial.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.application.impl.HatenaServiceImpl
import me.rei_m.hbfavmaterial.application.impl.TwitterServiceImpl
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.infra.network.SignedRetrofitFactory
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideHatenaOAuthManager(httpClient: OkHttpClient): HatenaOAuthManager {
        return HatenaOAuthManager(application.getString(R.string.api_key_hatena_consumer_key),
                application.getString(R.string.api_key_hatena_consumer_secret),
                httpClient)
    }

    @Provides
    @Singleton
    fun provideTwitterService(): TwitterService {
        return TwitterServiceImpl(application.getSharedPreferences("TwitterModel", Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideHatenaService(hatenaOAuthManager: HatenaOAuthManager,
                             signedRetrofitFactory: SignedRetrofitFactory): HatenaService {
        return HatenaServiceImpl(application.getSharedPreferences("HatenaModel", Context.MODE_PRIVATE), hatenaOAuthManager, signedRetrofitFactory)
    }
}
