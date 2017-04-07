package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.*

class EditUserIdDialogFragmentViewModel(private val userModel: UserModel,
                                        private val rxBus: RxBus,
                                        private val userIdErrorMessage: String) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.userUpdatedEvent.subscribe {
            idErrorMessage.set("")
            userId.set(it.id)
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
        userId.set(userModel.user.id)
    }

    fun onClickSetUp(view: View) {
        rxBus.send(ShowProgressDialogEvent())
        userModel.setUpUserId(userId.get())
    }

    fun onClickCancel(view: View) {
        rxBus.send(DismissEditHatenaIdDialogEvent())
    }
}
