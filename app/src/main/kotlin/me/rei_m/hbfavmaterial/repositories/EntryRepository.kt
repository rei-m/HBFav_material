package me.rei_m.hbfavmaterial.repositories

import kotlinx.dom.parseXml
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.network.HotEntryRss
import me.rei_m.hbfavmaterial.network.NewEntryRss
import me.rei_m.hbfavmaterial.utils.RssXmlUtil
import rx.Observable
import java.util.*

/**
 * エントリー情報のリポジトリ.
 */
open class EntryRepository {

    /**
     * ホッテントリ情報を取得する.
     */
    open fun findByEntryTypeForHot(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return HotEntryRss()
                .request(entryTypeFilter)
                .map { response -> parseRssResponse(response) }
    }

    /**
     * 新着エントリ情報を取得する.
     */
    open fun findByEntryTypeForNew(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return NewEntryRss()
                .request(entryTypeFilter)
                .map { response -> parseRssResponse(response) }
    }

    /**
     * レスポンスをパースしてエントリ情報に変換する.
     */
    private fun parseRssResponse(response: String): List<EntryEntity> {
        return ArrayList<EntryEntity>().apply {
            val document = parseXml(response.byteInputStream())
            val feeds = document.getElementsByTagName("item")
            val feedCount = feeds.length
            for (i_feed in 0..feedCount - 1) {
                add(RssXmlUtil.createEntryFromFeed(feeds.item(i_feed)))
            }
        }
    }
}
