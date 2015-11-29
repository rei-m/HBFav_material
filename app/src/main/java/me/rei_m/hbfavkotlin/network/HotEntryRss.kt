package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavkotlin.entities.EntryEntity
import me.rei_m.hbfavkotlin.utils.ApiUtil
import me.rei_m.hbfavkotlin.utils.BookmarkUtil.Companion.EntryType
import rx.Observable

public final class HotEntryRss : AbsEntryRss() {

    public fun request(entryType: EntryType): Observable<EntryEntity> {

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

        return super.request(builder.build())
    }
}