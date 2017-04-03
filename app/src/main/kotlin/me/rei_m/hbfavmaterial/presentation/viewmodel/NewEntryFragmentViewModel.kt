package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.GetNewEntriesUsecase

class NewEntryFragmentViewModel(private val getNewEntriesUsecase: GetNewEntriesUsecase,
                                private val rxBus: RxBus,
                                private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    val entryList: ObservableArrayList<EntryEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var entryTypeFilter = EntryTypeFilter.ALL

    private var isLoading: Boolean = false

    override fun onResume() {
        super.onResume()

        isVisibleProgress.set(true)

        registerDisposable(getNewEntriesUsecase.get(entryTypeFilter).subscribeAsync({
            entryList.clear()
            if (it.isNotEmpty()) {
                entryList.addAll(it)
            } else {
                isVisibleEmpty.set(true)
            }
        }, {
            rxBus.send(FailToConnectionEvent())
        }, {
            isVisibleProgress.set(false)
        }))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigator.navigateToBookmark(entryList[position])
    }

    fun onRefresh() {
        if (isRefreshing.get() || isLoading) {
            return
        }

        isRefreshing.set(true)
        isLoading = true

        registerDisposable(getNewEntriesUsecase.get(entryTypeFilter).subscribeAsync({
            entryList.clear()
            if (it.isNotEmpty()) {
                entryList.addAll(it)
            } else {
                isVisibleEmpty.set(true)
            }
        }, {
            rxBus.send(FailToConnectionEvent())
        }, {
            isRefreshing.set(false)
            isLoading = false
        }))
    }

    fun onOptionItemSelected(entryTypeFilter: EntryTypeFilter) {

        if (isLoading || this.entryTypeFilter == entryTypeFilter) return

        this.entryTypeFilter = entryTypeFilter

        isLoading = true

        isVisibleProgress.set(true)

        registerDisposable(getNewEntriesUsecase.get(entryTypeFilter).subscribeAsync({
            entryList.clear()
            if (it.isNotEmpty()) {
                entryList.addAll(it)
            } else {
                isVisibleEmpty.set(true)
            }
        }, {
            rxBus.send(FailToConnectionEvent())
        }, {
            isVisibleProgress.set(false)
            isLoading = false
        }))
    }
}
