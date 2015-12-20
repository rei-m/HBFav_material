package me.rei_m.hbfavmaterial.network

import com.squareup.okhttp.CacheControl
import com.squareup.okhttp.HttpUrl
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.dom.parseXml
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.utils.RssXmlUtil
import rx.Observable
import java.net.HttpURLConnection

public abstract class AbsBookmarkRss {

    protected fun request(url: HttpUrl): Observable<BookmarkEntity> {

        return Observable.create { t ->

            val request = Request.Builder()
                    .url(url)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()

            val response = OkHttpClient().newCall(request).execute()

            if (response.code() == HttpURLConnection.HTTP_OK) {

                val document = parseXml(response.body().byteStream())

                val feeds = document.getElementsByTagName("item")

                val feedCount = feeds.length

                for (i_feed in 0..feedCount - 1) {
                    t.onNext(RssXmlUtil.createBookmarkFromFeed(feeds.item(i_feed)))
                }

            } else {
                t.onError(HTTPException(response.code()))
            }

            t.onCompleted()
        }
    }
}
