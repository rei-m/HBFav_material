package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavkotlin.entities.EntryEntity
import rx.Observable

public final class NewEntryRss : AbsEntryRss() {

    public fun request(): Observable<EntryEntity> {

        val url = HttpUrl.Builder()
                .scheme("http")
                .host("b.hatena.ne.jp")
                .addPathSegment("entrylist")
                .addQueryParameter("mode", "rss")
                .build()

        return super.request(url)
    }
}