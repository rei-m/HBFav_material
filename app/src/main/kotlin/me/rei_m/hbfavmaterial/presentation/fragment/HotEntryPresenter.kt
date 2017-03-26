package me.rei_m.hbfavmaterial.presentation.fragment

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.usecase.GetHotEntriesUsecase

class HotEntryPresenter(private val getHotEntriesUsecase: GetHotEntriesUsecase) : HotEntryContact.Actions {

    private lateinit var view: HotEntryContact.View

    private var disposable: CompositeDisposable? = null

    private var isLoading = false

    override var entryTypeFilter: EntryTypeFilter = EntryTypeFilter.ALL

    private var entryList: List<EntryEntity> = listOf()

    override fun onCreate(view: HotEntryContact.View,
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

    fun initializeListContents() {

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

        return getHotEntriesUsecase.get(entryTypeFilter).subscribeAsync({
            onFindByHotEntryByTypeSuccess(it)
        }, {
            onFindByHotEntryByTypeFailure(it)
        }, {
            isLoading = false
            view.hideProgress()
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

    @Suppress("UNUSED_PARAMETER")
    private fun onFindByHotEntryByTypeFailure(e: Throwable) {
        view.showNetworkErrorMessage()
    }
}
