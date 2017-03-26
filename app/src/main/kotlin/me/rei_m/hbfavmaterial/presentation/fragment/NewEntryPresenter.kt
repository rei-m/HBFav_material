package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.GetNewEntriesUsecase

class NewEntryPresenter(private val getNewEntriesUsecase: GetNewEntriesUsecase) : NewEntryContact.Actions {

    private lateinit var view: NewEntryContact.View

    private var disposable: CompositeDisposable? = null

    private var entryList: List<EntryEntity> = listOf()

    private var isLoading = false

    override var entryTypeFilter: EntryTypeFilter = EntryTypeFilter.ALL

    override fun onCreate(view: NewEntryContact.View,
                          entryTypeFilter: EntryTypeFilter) {
        this.view = view
        this.entryTypeFilter = entryTypeFilter
    }

    override fun onResume() {
        disposable = CompositeDisposable()
        if (entryList.isEmpty()) {
            initializeListContents()
        } else {
            view.showEntryList(entryList)
        }
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    override fun onClickEntry(entryEntity: EntryEntity) {
        view.navigateToBookmark(entryEntity)
    }

    private fun initializeListContents() {

        if (isLoading) return

        disposable?.let {
            view.showProgress()
            it.add(request())
        }
    }

    override fun onRefreshList() {

        if (isLoading) return

        disposable?.add(request())
    }

    override fun onOptionItemSelected(entryTypeFilter: EntryTypeFilter) {

        if (isLoading) return

        if (this.entryTypeFilter == entryTypeFilter) return

        disposable?.let {
            this.entryTypeFilter = entryTypeFilter
            it.add(request())
        }
    }

    private fun request(): Disposable? {

        isLoading = true

        return getNewEntriesUsecase.get(entryTypeFilter).subscribeAsync({
            onFindNewEntryByTypeSuccess(it)
        }, {
            onFindNewEntryByTypeFailure(it)
        }, {
            isLoading = false
            view.hideProgress()
        })
    }

    private fun onFindNewEntryByTypeSuccess(entryList: List<EntryEntity>) {

        this.entryList = entryList
        if (this.entryList.isEmpty()) {
            view.hideEntryList()
            view.showEmpty()
        } else {
            view.hideEmpty()
            view.showEntryList(entryList)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onFindNewEntryByTypeFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }
}
