package me.rei_m.hbfavmaterial.model

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
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

    private val isLoadingSubject = BehaviorSubject.create<Boolean>()
    private val isRefreshingSubject = BehaviorSubject.create<Boolean>()
    private val entryListSubject = BehaviorSubject.create<List<EntryEntity>>()
    private val entryTypeFilterSubject = BehaviorSubject.create<EntryTypeFilter>()
    private val isRaisedGetErrorSubject = BehaviorSubject.create<Boolean>()

    private val isRaisedRefreshErrorSubject = PublishSubject.create<Unit>()

    val isLoading: Observable<Boolean> = isLoadingSubject
    val isRefreshing: Observable<Boolean> = isRefreshingSubject
    val entryList: Observable<List<EntryEntity>> = entryListSubject
    val entryTypeFilter: Observable<EntryTypeFilter> = entryTypeFilterSubject
    val isRaisedGetError: Observable<Boolean> = isRaisedGetErrorSubject

    val isRaisedRefreshError: Observable<Unit> = isRaisedRefreshErrorSubject

    init {
        isLoadingSubject.onNext(false)
        isRefreshingSubject.onNext(false)
        entryTypeFilterSubject.onNext(EntryTypeFilter.ALL)
    }

    fun getList(entryTypeFilter: EntryTypeFilter) {

        if (isLoadingSubject.value) {
            return
        }

        isLoadingSubject.onNext(true)

        fetch(entryTypeFilter).subscribeAsync({
            entryListSubject.onNext(listOf())
            entryListSubject.onNext(it)
            if (entryTypeFilterSubject.value != entryTypeFilter) {
                entryTypeFilterSubject.onNext(entryTypeFilter)
            }
            isRaisedGetErrorSubject.onNext(false)
        }, {
            isRaisedGetErrorSubject.onNext(true)
        }, {
            isLoadingSubject.onNext(false)
        })
    }

    fun refreshList() {

        if (isRefreshingSubject.value) {
            return
        }

        isRefreshingSubject.onNext(true)

        fetch(entryTypeFilterSubject.value).subscribeAsync({
            entryListSubject.onNext(listOf())
            entryListSubject.onNext(it)
            isRaisedGetErrorSubject.onNext(false)
        }, {
            isRaisedRefreshErrorSubject.onNext(Unit)
        }, {
            isRefreshingSubject.onNext(false)
        })
    }

    private fun fetch(entryTypeFilter: EntryTypeFilter): Single<List<EntryEntity>> {
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
                        article = articleEntity,
                        description = it.description,
                        date = RssXmlUtil.parseStringToDate(it.dateString),
                        subject = it.subject)
            }
        }
    }
}
