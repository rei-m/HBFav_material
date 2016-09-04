package me.rei_m.hbfavmaterial.infra.network

import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer

object RetrofitManager {

    private const val BASE_URL = "http://b.hatena.ne.jp"

    private const val BASE_URL_HOT_ENTRY_All = "http://feeds.feedburner.com"

    private const val BASE_URL_OAUTH = "http://api.b.hatena.ne.jp/1/"

    val json: Retrofit

    val xml: Retrofit

    val xmlForHotEntryAll: Retrofit

    val scalar: Retrofit

    fun createOAuthRetrofit(consumer: OkHttpOAuthConsumer): Retrofit {
        return createRetrofit(BASE_URL_OAUTH,
                HttpClientBuilder.newSignedBuilder(consumer).build(),
                GsonConverterFactory.create())
    }

    init {
        json = createRetrofit(BASE_URL,
                HttpClient.instance,
                GsonConverterFactory.create())

        xml = createRetrofit(BASE_URL,
                HttpClient.instance,
                SimpleXmlConverterFactory.create())

        xmlForHotEntryAll = createRetrofit(BASE_URL_HOT_ENTRY_All,
                HttpClient.instance,
                SimpleXmlConverterFactory.create())

        scalar = createRetrofit(BASE_URL,
                HttpClient.instance,
                ScalarsConverterFactory.create())
    }

    private fun createRetrofit(baseUrl: String,
                               client: OkHttpClient,
                               converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(converterFactory)
                .build()
    }
}
