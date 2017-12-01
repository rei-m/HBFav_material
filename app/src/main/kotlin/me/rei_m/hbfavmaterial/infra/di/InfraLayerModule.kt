/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
