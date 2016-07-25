package me.rei_m.hbfavmaterial.fragments.presenter

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.fragments.BaseFragment
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.service.EntryService
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class HotEntryPresenter(private val view: HotEntryContact.View,
                        var entryTypeFilter: EntryTypeFilter) : HotEntryContact.Actions {

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var entryService: EntryService

    private val entryList: MutableList<EntryEntity> = mutableListOf()

    private var isLoading = false

    init {
        (view as BaseFragment).component.inject(this)
    }

    override fun clickEntry(entryEntity: EntryEntity) {
        val activity = (view as Fragment).activity
        navigator.navigateToBookmark(activity, entryEntity)
    }

    override fun initializeListContents(): Subscription? {

        if (isLoading) return null

        view.showProgress()

        return request()
    }

    override fun fetchListContents(): Subscription? {

        if (isLoading) return null

        return request()
    }

    override fun toggleListContents(entryTypeFilter: EntryTypeFilter): Subscription? {

        if (isLoading) return null

        if (this.entryTypeFilter == entryTypeFilter) return null

        this.entryTypeFilter = entryTypeFilter

        return request()
    }

    private fun request(): Subscription? {

        isLoading = true

        val observer = object : Observer<List<EntryEntity>> {
            override fun onNext(t: List<EntryEntity>?) {
                t ?: return

                entryList.clear()
                entryList.addAll(t)

                if (entryList.isEmpty()) {
                    view.hideEntryList()
                    view.showEmpty()
                } else {
                    view.hideEmpty()
                    view.showEntryList(entryList)
                }
            }

            override fun onCompleted() {
            }

            override fun onError(e: Throwable?) {
                view.showNetworkErrorMessage()
            }
        }

        return entryService.findHotEntryByType(entryTypeFilter)
                .doOnUnsubscribe {
                    isLoading = false
                    view.hideProgress()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}
