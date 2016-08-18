package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import me.rei_m.hbfavmaterial.service.EntryService
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class NewEntryPresenter(private val entryService: EntryService) : NewEntryContact.Actions {

    private lateinit var view: NewEntryContact.View

    private var subscription: CompositeSubscription? = null

    private val entryList: MutableList<EntryEntity> = mutableListOf()

    private var isLoading = false

    override var entryTypeFilter: EntryTypeFilter = EntryTypeFilter.ALL

    override fun onCreate(view: NewEntryContact.View,
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

    private fun initializeListContents() {

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

        return entryService.findNewEntryByType(entryTypeFilter)
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
                    onFindNewEntryByTypeSuccess(it)
                }, {
                    onFindNewEntryByTypeFailure(it)
                })
    }

    private fun onFindNewEntryByTypeSuccess(entryList: List<EntryEntity>) {

        this.entryList.clear()
        this.entryList.addAll(entryList)

        if (this.entryList.isEmpty()) {
            view.hideEntryList()
            view.showEmpty()
        } else {
            view.hideEmpty()
            view.showEntryList(entryList)
        }
    }

    private fun onFindNewEntryByTypeFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }
}
