package me.rei_m.hbfavmaterial.network

import okhttp3.OkHttpClient

object HttpClient {
    val instance: OkHttpClient = HttpClientBuilder.newBuilder().build()
}
