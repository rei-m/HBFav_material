package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.network.HatenaRssService
import me.rei_m.hbfavmaterial.network.RetrofitManager
import me.rei_m.hbfavmaterial.utils.ApiUtil
import me.rei_m.hbfavmaterial.utils.RssXmlUtil
import org.jsoup.Jsoup
import rx.Observable

/**
 * エントリー情報のリポジトリ.
 */
open class EntryRepository {

    /**
     * ホッテントリ情報を取得する.
     */
    open fun findByEntryTypeForHot(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {

        val rss = if (entryTypeFilter == EntryTypeFilter.ALL)
            RetrofitManager.xmlForHotEntryAll.create(HatenaRssService::class.java).hotentry()
        else
            RetrofitManager.xml.create(HatenaRssService::class.java).hotentry(ApiUtil.getEntryTypeRss(entryTypeFilter))

        return rss.map { response ->
            response.list.map {
                val parsedContent = Jsoup.parse(it.content)
                val articleEntity = ArticleEntity(
                        title = it.title,
                        url = it.link,
                        bookmarkCount = it.bookmarkCount,
                        iconUrl = RssXmlUtil.extractArticleIcon(parsedContent),
                        body = RssXmlUtil.extractArticleBodyForEntry(parsedContent),
                        bodyImageUrl = RssXmlUtil.extractArticleImageUrl(parsedContent))
                EntryEntity(
                        articleEntity = articleEntity,
                        description = it.description,
                        date = RssXmlUtil.parseStringToDate(it.dateString),
                        subject = it.subject)
            }
        }
    }

    /**
     * 新着エントリ情報を取得する.
     */
    open fun findByEntryTypeForNew(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {

        val rss = if (entryTypeFilter == EntryTypeFilter.ALL)
            RetrofitManager.xml.create(HatenaRssService::class.java).new()
        else
            RetrofitManager.xml.create(HatenaRssService::class.java).new(ApiUtil.getEntryTypeRss(entryTypeFilter))

        return rss.map { response ->
            response.list.map {
                val parsedContent = Jsoup.parse(it.content)
                val articleEntity = ArticleEntity(
                        title = it.title,
                        url = it.link,
                        bookmarkCount = it.bookmarkCount,
                        iconUrl = RssXmlUtil.extractArticleIcon(parsedContent),
                        body = RssXmlUtil.extractArticleBodyForEntry(parsedContent),
                        bodyImageUrl = RssXmlUtil.extractArticleImageUrl(parsedContent))
                EntryEntity(
                        articleEntity = articleEntity,
                        description = it.description,
                        date = RssXmlUtil.parseStringToDate(it.dateString),
                        subject = it.subject)
            }
        }
    }
}
