package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import me.rei_m.hbfavmaterial.model.NewEntryModel
import me.rei_m.hbfavmaterial.model.entity.EntryEntity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class NewEntryFragmentViewModel(private val newEntryModel: NewEntryModel,
                                private val navigator: Navigator) : AbsFragmentViewModel() {

    val entryList: ObservableArrayList<EntryEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var entryTypeFilter = EntryTypeFilter.ALL
        private set

    private var updateFilterEventSubject = PublishSubject.create<Unit>()
    val updateFilterEvent: io.reactivex.Observable<Unit> = updateFilterEventSubject

    private var snackbarFactory: SnackbarFactory? = null

    fun onCreate(entryTypeFilter: EntryTypeFilter) {
        this.entryTypeFilter = entryTypeFilter
    }

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

    override fun onStart() {
        super.onStart()
        registerDisposable(newEntryModel.entryListUpdatedEvent.subscribe {
            entryList.clear()
            entryList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
        }, newEntryModel.error.subscribe {
            isVisibleProgress.set(false)
            isRefreshing.set(false)
            snackbarFactory?.create(R.string.message_error_network)?.show()
        }, newEntryModel.entryTypeFilterUpdatedEvent.subscribe {
            entryTypeFilter = it
            updateFilterEventSubject.onNext(Unit)
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

    fun onDestroyView() {
        snackbarFactory = null
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
