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

package me.rei_m.hbfavmaterial.infra.network

import me.rei_m.hbfavmaterial.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

object HttpClientBuilder {

    private val instance: OkHttpClient = OkHttpClient()

    fun newBuilder(): OkHttpClient.Builder {

        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        return instance.newBuilder()
                .addInterceptor(CacheControlInterceptor())
                .addInterceptor(logging)
    }

    fun newSignedBuilder(consumer: OkHttpOAuthConsumer): OkHttpClient.Builder {

        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        return HttpClientBuilder.newBuilder()
                .addInterceptor(SigningInterceptor(consumer))
                .addInterceptor(CacheControlInterceptor())
                .addInterceptor(logging)
    }
}
