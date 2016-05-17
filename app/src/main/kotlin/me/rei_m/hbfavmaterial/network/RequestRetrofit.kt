package me.rei_m.hbfavmaterial.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RequestRetrofit {
    companion object {

        const val BASE_URL = "http://b.hatena.ne.jp"

        fun newInstance(): Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(HttpClient.instance)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
    }
}