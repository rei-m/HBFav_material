package me.rei_m.hbfavmaterial.network

import com.squareup.okhttp.HttpUrl
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.utils.ApiUtil
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import rx.Observable

/**
 * 新着エントリーをRSSから取得するクラス.
 */
public class NewEntryRss : AbsEntryRss() {

    public fun request(entryType: EntryType): Observable<EntryEntity> {

        val builder = HttpUrl.Builder().scheme("http").host("b.hatena.ne.jp")

        if (entryType == EntryType.ALL) {
            builder.addPathSegment("entrylist.rss")
        } else {
            builder.addPathSegment("entrylist")
                    .addPathSegment(ApiUtil.getEntryTypeRss(entryType))
        }

        return super.request(builder.build())
    }
}
