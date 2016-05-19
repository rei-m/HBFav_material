package me.rei_m.hbfavmaterial.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object HttpClient {

    val instance: OkHttpClient

    init {

        // TODO ビルドタイプかフレーバーでロガーのレベルを変える.
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        instance = HttpClientBuilder.instance.newBuilder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                            .addHeader("Cache-Control", "no-cache")
                            .addHeader("Cache-Control", "no-store")
                            .method(original.method(), original.body())
                    val request = requestBuilder.build()

                    return@addInterceptor chain.proceed(request)
                }
                .addInterceptor(logging)
                .build()
    }
}
