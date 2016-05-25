package me.rei_m.hbfavmaterial.network

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

object RetrofitManager {

    private const val BASE_URL = "http://b.hatena.ne.jp"

    private const val BASE_URL_HOT_ENTRY_All = "http://feeds.feedburner.com"

    private const val BASE_URL_OAUTH = "http://api.b.hatena.ne.jp/1/"

    val json: Retrofit

    val xml: Retrofit

    val xmlForHotEntryAll: Retrofit

    val scalar: Retrofit

    fun oauthRetrofitFactory(consumer: OkHttpOAuthConsumer): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = HttpClientBuilder.instance.newBuilder()
                .addInterceptor(SigningInterceptor(consumer))
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
        return Retrofit.Builder()
                .baseUrl(BASE_URL_OAUTH)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

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