package me.rei_m.hbfavmaterial.viewmodel.activity

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.ShowBookmarkEditEvent
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class BookmarkActivityViewModel(private val hatenaService: HatenaService,
                                private val rxBus: RxBus,
                                private val navigator: Navigator) : AbsActivityViewModel() {

    val entryTitle: ObservableField<String> = ObservableField()

    val entryLink: ObservableField<String> = ObservableField()

    override fun onStart() {
        super.onStart()
        registerDisposable(hatenaService.confirmAuthorisedEvent.subscribe {
            if (it) {
                rxBus.send(ShowBookmarkEditEvent(entryTitle.get(), entryLink.get()))
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
