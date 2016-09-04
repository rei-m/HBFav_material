package me.rei_m.hbfavmaterial.infra.network

import okhttp3.Interceptor
import okhttp3.Response

class CacheControlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cache-Control", "no-store")
                .method(original.method(), original.body())
        val request = requestBuilder.build()

        return chain.proceed(request)
    }
}