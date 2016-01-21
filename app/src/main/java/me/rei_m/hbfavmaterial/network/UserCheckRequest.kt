package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.exeptions.HTTPException
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.Observable
import java.net.HttpURLConnection

/**
 * ユーザーIDが有効か問い合わせるクラス.
 */
class UserCheckRequest {

    fun request(userId: String): Observable<Boolean> {

        val url = HttpUrl.Builder()
                .scheme("http")
                .host("b.hatena.ne.jp")
                .addPathSegment(userId)
                .build()

        return Observable.create { t ->

            val request = Request.Builder()
                    .url(url)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = OkHttpClient().newCall(request).execute()

            if (response.code() == HttpURLConnection.HTTP_OK) {
                // 原因はわからないがカンマ等の記号が入っている場合にTopページを取得しているケースがある
                // 基本的には入力時に弾く予定だが、Modelの仕様としては考慮してトップページが返ってきたら
                // 存在しないユーザー = 404として扱う
                val isFetchedTop = response.body().string().contains("<title>はてなブックマーク</title>")
                t.onNext(!isFetchedTop)
            } else if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                t.onNext(false)
            } else {
                t.onError(HTTPException(response.code()))
            }

            t.onCompleted()
        }
    }
}
