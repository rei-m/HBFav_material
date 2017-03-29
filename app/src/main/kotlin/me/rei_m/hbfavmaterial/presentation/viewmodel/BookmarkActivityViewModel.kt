package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.ShowBookmarkEditEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.DisplayBookmarkEditFormUsecase
import me.rei_m.hbfavmaterial.exception.HatenaUnAuthorizedException

class BookmarkActivityViewModel(private val displayBookmarkEditFormUsecase: DisplayBookmarkEditFormUsecase,
                                private val rxBus: RxBus,
                                private val navigator: ActivityNavigator) : AbsActivityViewModel() {

    val entryTitle: ObservableField<String> = ObservableField()

    val entryLink: ObservableField<String> = ObservableField()

    private var isLoading: Boolean = false
    
    fun onClickFab(view: View) {

        if (isLoading) {
            return
        }

        isLoading = true

        registerDisposable(displayBookmarkEditFormUsecase.execute(entryLink.get()).subscribeAsync({
            rxBus.send(ShowBookmarkEditEvent(entryTitle.get(), it))
        }, {
            when (it) {
                is HatenaUnAuthorizedException -> {
                    navigator.navigateToOAuth()
                }
                else -> {
                    rxBus.send(FailToConnectionEvent())
                }
            }
        }, {
            isLoading = false
        }))
    }

    fun onAuthoriseHatena() {
        registerDisposable(displayBookmarkEditFormUsecase.execute(entryLink.get()).subscribeAsync({
            rxBus.send(ShowBookmarkEditEvent(entryTitle.get(), it))
        }, {
            when (it) {
                is HatenaUnAuthorizedException -> {
                    navigator.navigateToOAuth()
                }
                else -> {
                    rxBus.send(FailToConnectionEvent())
                }
            }
        }))
    }
}
