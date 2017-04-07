package me.rei_m.hbfavmaterial.infra.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer

class SignedRetrofitFactory {

    companion object {
        private const val BASE_URL_OAUTH = "http://api.b.hatena.ne.jp/1/"
    }

    fun create(consumer: OkHttpOAuthConsumer): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL_OAUTH)
                .client(HttpClientBuilder.newSignedBuilder(consumer).build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}
