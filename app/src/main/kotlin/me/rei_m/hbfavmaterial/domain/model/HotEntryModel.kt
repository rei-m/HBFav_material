package me.rei_m.hbfavmaterial.domain.model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.ArticleEntity
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.domain.util.ApiUtil
import me.rei_m.hbfavmaterial.domain.util.RssXmlUtil
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import org.jsoup.Jsoup

class HotEntryModel(private val hatenaRssService: HatenaRssService,
                    private val hotEntryRssService: HatenaRssService) {

    private val entryListSubject = PublishSubject.create<List<EntryEntity>>()

    val entryList: Observable<List<EntryEntity>> = entryListSubject

    private var entryTypeFilterSubject = PublishSubject.create<EntryTypeFilter>()

    val entryTypeFilter: Observable<EntryTypeFilter> = entryTypeFilterSubject

    private val errorSubject = PublishSubject.create<Unit>()

    val error: Observable<Unit> = errorSubject

    private var isLoading: Boolean = false

    fun getList(entryTypeFilter: EntryTypeFilter) {

        if (isLoading) {
            return
        }

        isLoading = true

        val rss = if (entryTypeFilter == EntryTypeFilter.ALL) {
            hotEntryRssService.hotentry()
        } else {
            hatenaRssService.hotentry(ApiUtil.getEntryTypeRss(entryTypeFilter))
        }

        rss.map { response ->
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
                        article = articleEntity,
                        description = it.description,
                        date = RssXmlUtil.parseStringToDate(it.dateString),
                        subject = it.subject)
            }
        }.subscribeAsync({
            entryTypeFilterSubject.onNext(entryTypeFilter)
            entryListSubject.onNext(it)
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }
}
