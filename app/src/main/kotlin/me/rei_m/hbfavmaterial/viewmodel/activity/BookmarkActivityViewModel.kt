package me.rei_m.hbfavmaterial.viewmodel.activity

import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class BookmarkActivityViewModel(private val hatenaService: HatenaService,
                                private val navigator: Navigator) : AbsActivityViewModel() {

    val entryTitle: ObservableField<String> = ObservableField()

    val entryLink: ObservableField<String> = ObservableField()

    private var showBookmarkEditEventSubject = PublishSubject.create<Unit>()
    val showBookmarkEditEvent: Observable<Unit> = showBookmarkEditEventSubject

    override fun onStart() {
        super.onStart()
        registerDisposable(hatenaService.confirmAuthorisedEvent.subscribe {
            if (it) {
                showBookmarkEditEventSubject.onNext(Unit)
            } else {
                navigator.navigateToOAuth()
            }
        })
    }

    fun onClickFab(view: View) {
        hatenaService.confirmAuthorised()
    }

    fun onAuthoriseHatena() {
        hatenaService.confirmAuthorised()
    }
}
