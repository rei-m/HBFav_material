package me.rei_m.hbfavmaterial.infra.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.infra.network.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
open class InfraLayerModule {

    companion object {
        private const val BASE_URL = "http://b.hatena.ne.jp"

        private const val BASE_URL_HOT_ENTRY_All = "http://feeds.feedburner.com"
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = HttpClientBuilder.newBuilder().build()

    @Singleton
    @Provides
    fun provideHatenaBookmarkService(client: OkHttpClient): HatenaBookmarkService {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(HatenaBookmarkService::class.java)
    }

    @Singleton
    @Provides
    fun provideHatenaApiService(client: OkHttpClient): HatenaApiService {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HatenaApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideHatenaRssService(client: OkHttpClient): HatenaRssService {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(HatenaRssService::class.java)
    }

    @Singleton
    @Provides
    fun provideHatenaHotEntryRssService(client: OkHttpClient): HatenaHotEntryRssService {
        return Retrofit.Builder()
                .baseUrl(BASE_URL_HOT_ENTRY_All)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(HatenaHotEntryRssService::class.java)
    }

    @Singleton
    @Provides
    fun provideSignedRetrofitFactory(): SignedRetrofitFactory {
        return SignedRetrofitFactory()
    }
}
