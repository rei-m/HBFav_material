package me.rei_m.hbfavmaterial.presentation.fragment

import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.usecase.GetHotEntriesUsecase
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class HotEntryPresenter(private val getHotEntriesUsecase: GetHotEntriesUsecase) : HotEntryContact.Actions {

    private lateinit var view: HotEntryContact.View

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override var entryTypeFilter: EntryTypeFilter = EntryTypeFilter.ALL

    private var entryList: List<EntryEntity> = listOf()

    override fun onCreate(view: HotEntryContact.View,
                          entryTypeFilter: EntryTypeFilter) {
        this.view = view
        this.entryTypeFilter = entryTypeFilter
    }

    override fun onResume() {
        subscription = CompositeSubscription()
        if (entryList.isEmpty()) {
            initializeListContents()
        } else {
            view.showEntryList(entryList)
        }
    }

    override fun onPause() {
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onClickEntry(entryEntity: EntryEntity) {
        view.navigateToBookmark(entryEntity)
    }

    fun initializeListContents() {

        if (isLoading) return

        subscription?.let {
            view.showProgress()
            it.add(request())
        }
    }

    override fun onRefreshList() {

        if (isLoading) return

        subscription?.add(request())
    }

    override fun onOptionItemSelected(entryTypeFilter: EntryTypeFilter) {

        if (isLoading) return

        if (this.entryTypeFilter == entryTypeFilter) return

        subscription?.let {
            this.entryTypeFilter = entryTypeFilter
            it.add(request())
        }
    }

    private fun request(): Subscription? {

        return getHotEntriesUsecase.get(entryTypeFilter)
                .doOnSubscribe {
                    isLoading = true
                }
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onFindByHotEntryByTypeSuccess(it)
                }, {
                    onFindByHotEntryByTypeFailure(it)
                })
    }

    private fun onFindByHotEntryByTypeSuccess(entryList: List<EntryEntity>) {
        this.entryList = entryList
        if (this.entryList.isEmpty()) {
            view.hideEntryList()
            view.showEmpty()
        } else {
            view.hideEmpty()
            view.showEntryList(this.entryList)
        }
    }

    private fun onFindByHotEntryByTypeFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }
}
