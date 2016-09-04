package me.rei_m.hbfavmaterial.infra.network

import okhttp3.OkHttpClient

object HttpClient {
    val instance: OkHttpClient = HttpClientBuilder.newBuilder().build()
}
