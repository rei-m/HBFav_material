package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.DismissProgressDialogEvent
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.FinishActivityEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class InitializeFragmentViewModel(private val userModel: UserModel,
                                  private val rxBus: RxBus,
                                  private val navigator: Navigator,
                                  private val userIdErrorMessage: String) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.userUpdatedEvent.subscribe {
            idErrorMessage.set("")
            rxBus.send(FinishActivityEvent())
            rxBus.send(DismissProgressDialogEvent())
            navigator.navigateToMain()
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
        if (userModel.user.isCompleteSetting) {
            navigator.navigateToMain()
            rxBus.send(FinishActivityEvent())
        }
    }

    fun onClickButtonSetId(view: View) {
        userModel.setUpUserId(userId.get())
    }
}
