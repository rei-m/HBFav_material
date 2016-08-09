package me.rei_m.hbfavmaterial.service.impl

import me.rei_m.hbfavmaterial.entity.ArticleEntity
import me.rei_m.hbfavmaterial.entity.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import me.rei_m.hbfavmaterial.network.HatenaRssService
import me.rei_m.hbfavmaterial.network.RetrofitManager
import me.rei_m.hbfavmaterial.service.EntryService
import me.rei_m.hbfavmaterial.util.ApiUtil
import me.rei_m.hbfavmaterial.util.RssXmlUtil
import org.jsoup.Jsoup
import rx.Observable

class EntryServiceImpl : EntryService {

    override fun findHotEntryByType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
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

    override fun findNewEntryByType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
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
