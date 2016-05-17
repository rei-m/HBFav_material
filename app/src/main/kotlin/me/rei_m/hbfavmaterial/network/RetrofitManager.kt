package me.rei_m.hbfavmaterial.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitManager {

    private const val BASE_URL = "http://b.hatena.ne.jp"

    private const val BASE_URL_HOT_ENTRY_All = "http://feeds.feedburner.com"

    val json: Retrofit

    val xml: Retrofit

    val xmlForHotEntryAll: Retrofit

    val scalar: Retrofit

    init {
        json = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        xml = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        xmlForHotEntryAll = Retrofit.Builder()
                .baseUrl(BASE_URL_HOT_ENTRY_All)
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        scalar = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
    }
}