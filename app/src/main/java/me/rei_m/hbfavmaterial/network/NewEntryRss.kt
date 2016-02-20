package me.rei_m.hbfavmaterial.network

import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.utils.ApiUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.Request
import rx.Observable
import java.net.HttpURLConnection

/**
 * 新着エントリーをRSSから取得するクラス.
 */
class NewEntryRss {

    fun request(entryType: EntryType): Observable<String> {

        return Observable.create { t ->

            val builder = HttpUrl.Builder().scheme("http").host("b.hatena.ne.jp")

            if (entryType == EntryType.ALL) {
                builder.addPathSegment("entrylist.rss")
            } else {
                builder.addPathSegment("entrylist")
                        .addPathSegment(ApiUtil.getEntryTypeRss(entryType))
            }

            val url = builder.build()

            val request = Request.Builder()
                    .url(url)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = HttpClient.instance.newCall(request).execute()
            if (response.code() == HttpURLConnection.HTTP_OK) {
                t.onNext(response.body().string())
                t.onCompleted()
            } else {
                t.onError(HTTPException(response.code()))
            }
        }
    }
}
