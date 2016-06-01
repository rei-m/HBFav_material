package me.rei_m.hbfavmaterial.network

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
            logging.level = HttpLoggingInterceptor.Level.BODY;
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE;
        }

        return instance.newBuilder()
                .addInterceptor(CacheControlInterceptor())
                .addInterceptor(logging)
    }

    fun newSignedBuilder(consumer: OkHttpOAuthConsumer): OkHttpClient.Builder {

        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY;
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE;
        }
        
        return HttpClientBuilder.newBuilder()
                .addInterceptor(SigningInterceptor(consumer))
                .addInterceptor(CacheControlInterceptor())
                .addInterceptor(logging)
    }
}
