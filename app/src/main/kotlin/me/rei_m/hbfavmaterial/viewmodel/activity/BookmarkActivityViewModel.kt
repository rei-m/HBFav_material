package me.rei_m.hbfavmaterial.viewmodel.activity

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.application.HatenaService

class BookmarkActivityViewModel(private val hatenaService: HatenaService) : ViewModel() {

    val entryTitle: ObservableField<String> = ObservableField()

    val entryLink: ObservableField<String> = ObservableField()

    private var showBookmarkEditEventSubject = PublishSubject.create<Unit>()
    val showBookmarkEditEvent: Observable<Unit> = showBookmarkEditEventSubject

    private var unauthorisedEventSubject = PublishSubject.create<Unit>()
    val unauthorisedEvent: Observable<Unit> = unauthorisedEventSubject

    private val disposable = CompositeDisposable()

    init {
        disposable.addAll(hatenaService.confirmAuthorisedEvent.subscribe {
            if (it) {
                showBookmarkEditEventSubject.onNext(Unit)
            } else {
                unauthorisedEventSubject.onNext(Unit)
            }
        })
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickFab(view: View) {
        hatenaService.confirmAuthorised()
    }

    fun onAuthoriseHatena() {
        hatenaService.confirmAuthorised()
    }

    class Factory(private val hatenaService: HatenaService) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookmarkActivityViewModel::class.java)) {
                return BookmarkActivityViewModel(hatenaService) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
