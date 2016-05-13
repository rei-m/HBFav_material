package me.rei_m.hbfavmaterial.network

import okhttp3.OkHttpClient

object HttpClient {
    val instance = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Cache-Control", "no-store")
                        .method(original.method(), original.body())
                val request = requestBuilder.build()

                return@addInterceptor chain.proceed(request)
            }
            .build()
}
