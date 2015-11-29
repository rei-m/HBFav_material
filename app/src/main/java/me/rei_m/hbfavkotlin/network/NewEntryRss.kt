package me.rei_m.hbfavkotlin.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavkotlin.entities.EntryEntity
import me.rei_m.hbfavkotlin.utils.ApiUtil
import me.rei_m.hbfavkotlin.utils.BookmarkUtil.Companion.EntryType
import rx.Observable

public final class NewEntryRss : AbsEntryRss() {

    public fun request(entryType: EntryType): Observable<EntryEntity> {

        val builder = HttpUrl.Builder().scheme("http").host("b.hatena.ne.jp")

        if (entryType == EntryType.ALL) {
            builder.addPathSegment("entrylist.rss")
        } else {
            builder.addPathSegment("entrylist")
            builder.addPathSegment(ApiUtil.getEntryTypeRss(entryType))
        }

        return super.request(builder.build())
    }
}