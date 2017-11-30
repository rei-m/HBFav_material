package me.rei_m.hbfavmaterial.viewmodel.widget.fragment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Observable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import android.widget.AdapterView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.model.HotEntryModel
import me.rei_m.hbfavmaterial.model.entity.EntryEntity

class HotEntryFragmentViewModel(private val hotEntryModel: HotEntryModel,
                                entryTypeFilter: EntryTypeFilter) : ViewModel() {

    val entryList: ObservableArrayList<EntryEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    val entryTypeFilter: ObservableField<EntryTypeFilter> = ObservableField(entryTypeFilter)

    val isVisibleError: ObservableBoolean = ObservableBoolean(false)

    private val onItemClickEventSubject = PublishSubject.create<EntryEntity>()
    val onItemClickEvent: io.reactivex.Observable<EntryEntity> = onItemClickEventSubject

    val onRaiseRefreshErrorEvent = hotEntryModel.isRaisedRefreshError

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val entryTypeFilterChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            hotEntryModel.getList(this@HotEntryFragmentViewModel.entryTypeFilter.get())
        }
    }

    init {
        disposable.addAll(hotEntryModel.entryList.subscribe {
            if (it.isEmpty()) {
                entryList.clear()
            } else {
                entryList.addAll(it)
            }
            isVisibleEmpty.set(entryList.isEmpty())
        }, hotEntryModel.isLoading.subscribe {
            isVisibleProgress.set(it)
        }, hotEntryModel.isRefreshing.subscribe {
            isRefreshing.set(it)
        }, hotEntryModel.isRaisedGetError.subscribe {
            isVisibleError.set(it)
        })

        this.entryTypeFilter.addOnPropertyChangedCallback(entryTypeFilterChangedCallback)

        hotEntryModel.getList(this.entryTypeFilter.get())
    }

    override fun onCleared() {
        entryTypeFilter.removeOnPropertyChangedCallback(entryTypeFilterChangedCallback)
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemClickEventSubject.onNext(entryList[position])
    }

    fun onRefresh() {
        hotEntryModel.refreshList()
    }

    fun onOptionItemSelected(entryTypeFilter: EntryTypeFilter) {
        this.entryTypeFilter.set(entryTypeFilter)
    }

    class Factory(private val hotEntryModel: HotEntryModel,
                  var entryTypeFilter: EntryTypeFilter = EntryTypeFilter.ALL) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HotEntryFragmentViewModel::class.java)) {
                return HotEntryFragmentViewModel(hotEntryModel, entryTypeFilter) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}

