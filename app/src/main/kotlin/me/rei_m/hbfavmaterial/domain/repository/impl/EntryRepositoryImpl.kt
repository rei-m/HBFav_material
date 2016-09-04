package me.rei_m.hbfavmaterial.domain.repository.impl

import me.rei_m.hbfavmaterial.domain.entity.ArticleEntity
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.domain.repository.EntryRepository
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.util.ApiUtil
import me.rei_m.hbfavmaterial.util.RssXmlUtil
import org.jsoup.Jsoup
import rx.Observable

class EntryRepositoryImpl(private val hatenaRssService: HatenaRssService,
                          private val hotEntryRssService: HatenaRssService) : EntryRepository {

    override fun findHotByEntryType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        val rss = if (entryTypeFilter == EntryTypeFilter.ALL) {
            hotEntryRssService.hotentry()
        } else {
            hatenaRssService.hotentry(ApiUtil.getEntryTypeRss(entryTypeFilter))
        }

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

    override fun findNewByEntryType(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        val rss = if (entryTypeFilter == EntryTypeFilter.ALL) {
            hatenaRssService.new()
        } else {
            hatenaRssService.new(ApiUtil.getEntryTypeRss(entryTypeFilter))
        }

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
