package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavkotlin.entities.EntryEntity
import rx.Observable

public final class HotEntryRss : AbsEntryRss() {

    public fun request(): Observable<EntryEntity> {

        val url = HttpUrl.Builder()
                .scheme("http")
                .host("feeds.feedburner.com")
                .addPathSegment("hatena")
                .addPathSegment("b")
                .addPathSegment("hotentry")
                .build()

        return super.request(url)
    }
}