package me.rei_m.hbfavmaterial.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class RssRetrofit {
    companion object {
        fun newInstance(): Retrofit = Retrofit.Builder()
                .baseUrl("http://b.hatena.ne.jp")
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
    }
}