package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import me.rei_m.hbfavkotlin.exeptions.HTTPException
import rx.Observable
import java.net.HttpURLConnection

public class UserCheckRequest private constructor() {
    companion object {
        public fun request(userId: String): Observable<Boolean> {

            val url = HttpUrl.Builder()
                    .scheme("http")
                    .host("b.hatena.ne.jp")
                    .addPathSegment(userId)
                    .build()

            return Observable.create({ t ->

                val request = Request.Builder()
                        .url(url)
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build()

                val response = OkHttpClient().newCall(request).execute()

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    t.onNext(true)
                } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    t.onNext(false)
                } else {
                    t.onError(HTTPException(response.code()))
                }

                t.onCompleted()
            })
        }
    }
}