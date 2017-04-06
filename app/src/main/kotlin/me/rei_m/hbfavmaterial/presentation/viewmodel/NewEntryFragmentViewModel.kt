package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.domain.model.NewEntryModel
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.UpdateMainPageFilterEvent
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class NewEntryFragmentViewModel(private val newEntryModel: NewEntryModel,
                                private val rxBus: RxBus,
                                private val navigator: Navigator) : AbsFragmentViewModel() {

    val entryList: ObservableArrayList<EntryEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var entryTypeFilter = EntryTypeFilter.ALL

    override fun onStart() {
        super.onStart()
        registerDisposable(newEntryModel.entryListUpdatedEvent.subscribe {
            entryList.clear()
            entryList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
            rxBus.send(UpdateMainPageFilterEvent())
        }, newEntryModel.error.subscribe {
            rxBus.send(FailToConnectionEvent())
        }, newEntryModel.entryTypeFilterUpdatedEvent.subscribe {
            entryTypeFilter = it
        })
    }

    override fun onResume() {
        super.onResume()
        if (entryList.isEmpty()) {
            isVisibleProgress.set(true)
            newEntryModel.getList(entryTypeFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        isVisibleProgress.set(false)
        isRefreshing.set(false)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigator.navigateToBookmark(entryList[position])
    }

    fun onRefresh() {
        isRefreshing.set(true)
        newEntryModel.getList(entryTypeFilter)
    }

    fun onOptionItemSelected(entryTypeFilter: EntryTypeFilter) {
        if (this.entryTypeFilter == entryTypeFilter) return
        isVisibleProgress.set(true)
        newEntryModel.getList(entryTypeFilter)
    }
}
