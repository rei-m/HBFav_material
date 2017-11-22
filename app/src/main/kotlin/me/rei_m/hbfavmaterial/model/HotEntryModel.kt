package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaHotEntryRssService
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.model.entity.ArticleEntity
import me.rei_m.hbfavmaterial.model.entity.EntryEntity
import me.rei_m.hbfavmaterial.model.util.ApiUtil
import me.rei_m.hbfavmaterial.model.util.RssXmlUtil
import org.jsoup.Jsoup

class HotEntryModel(private val hatenaRssService: HatenaRssService,
                    private val hotEntryRssService: HatenaHotEntryRssService) {

    private var entryList: List<EntryEntity> = listOf()
        private set(value) {
            field = value
            entryListUpdatedEventSubject.onNext(value)
        }

    private var entryTypeFilter: EntryTypeFilter = EntryTypeFilter.ALL
        private set(value) {
            field = value
            entryTypeFilterUpdatedEventSubject.onNext(value)
        }

    private val entryListUpdatedEventSubject = PublishSubject.create<List<EntryEntity>>()
    private val entryTypeFilterUpdatedEventSubject = PublishSubject.create<EntryTypeFilter>()
    private val errorSubject = PublishSubject.create<Unit>()

    val entryListUpdatedEvent: Observable<List<EntryEntity>> = entryListUpdatedEventSubject
    val entryTypeFilterUpdatedEvent: Observable<EntryTypeFilter> = entryTypeFilterUpdatedEventSubject
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
            entryList = it
            if (this.entryTypeFilter != entryTypeFilter) {
                this.entryTypeFilter = entryTypeFilter
            }
        }, {
            errorSubject.onNext(Unit)
        }, {
            isLoading = false
        })
    }
}
