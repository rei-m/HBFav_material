package me.rei_m.hbfavmaterial.network

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.utils.ApiUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import rx.Observable
import java.net.HttpURLConnection

/**
 * ホッテントリをRSSから取得するクラス.
 */
class HotEntryRss {

    fun request(entryType: EntryType): Observable<String> {

        return Observable.create { t ->

            val builder = HttpUrl.Builder().scheme("http")

            if (entryType == EntryType.ALL) {
                builder.host("feeds.feedburner.com")
                        .addPathSegment("hatena")
                        .addPathSegment("b")
                        .addPathSegment("hotentry")
            } else {
                builder.host("b.hatena.ne.jp")
                        .addPathSegment("hotentry")
                        .addPathSegment(ApiUtil.getEntryTypeRss(entryType))
            }

            val url = builder.build()

            val request = Request.Builder()
                    .url(url)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = OkHttpClient().newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                t.onNext(response.body().string())
            } else {
                t.onError(HTTPException(response.code()))
            }

            t.onCompleted()
        }
    }
}
