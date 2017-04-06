package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.*

class EditUserIdDialogFragmentViewModel(private val userModel: UserModel,
                                        private val rxBus: RxBus,
                                        private val userIdErrorMessage: String) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.user.subscribe {
            userId.set(it.id)
        }, userModel.completeUpdateUserEvent.subscribe {
            idErrorMessage.set("")
            userId.set(it.id)
            rxBus.send(UpdateHatenaIdEvent(userId.get()))
            rxBus.send(DismissProgressDialogEvent())
            rxBus.send(DismissEditHatenaIdDialogEvent())
        }, userModel.unauthorisedEvent.subscribe {
            rxBus.send(DismissProgressDialogEvent())
            idErrorMessage.set(userIdErrorMessage)
        }, userModel.error.subscribe {
            rxBus.send(DismissProgressDialogEvent())
            rxBus.send(FailToConnectionEvent())
        })
    }

    override fun onResume() {
        super.onResume()
        userModel.getUser()
    }

    fun onClickSetUp(view: View) {
        rxBus.send(ShowProgressDialogEvent())
        userModel.setUpUserId(userId.get())
    }

    fun onClickCancel(view: View) {
        rxBus.send(DismissEditHatenaIdDialogEvent())
    }
}
