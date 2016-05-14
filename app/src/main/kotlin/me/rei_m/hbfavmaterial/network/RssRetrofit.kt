package me.rei_m.hbfavmaterial.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class RssRetrofit {
    companion object {

        const val BASE_URL = "http://b.hatena.ne.jp"

        const val BASE_URL_HOT_ENTRY_NO_TYPE = "http://feeds.feedburner.com"

        fun newInstance(baseUrl: String = BASE_URL): Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
    }
}