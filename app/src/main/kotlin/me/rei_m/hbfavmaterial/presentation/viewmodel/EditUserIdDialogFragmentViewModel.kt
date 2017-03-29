package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.exception.HatenaUnAuthorizedException
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.usecase.SetUpHatenaIdUsecase
import me.rei_m.hbfavmaterial.usecase.impl.DisplayEditUserIdDialogUsecase

class EditUserIdDialogFragmentViewModel(private val displayEditUserIdDialogUsecase: DisplayEditUserIdDialogUsecase,
                                        private val setUpHatenaIdUsecase: SetUpHatenaIdUsecase,
                                        private val rxBus: RxBus) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    private var isLoading = false

    private var originalUserId: String = ""

    override fun onResume() {
        super.onResume()
        registerDisposable(displayEditUserIdDialogUsecase.execute().subscribeAsync({
            userId.set(it)
            originalUserId = it
        }))
    }

    fun onClickSetUp(view: View) {

        if (userId.get() == originalUserId) {
            rxBus.send(DismissEditHatenaIdDialogEvent())
            idErrorMessage.set("")
            return
        }

        if (isLoading) {
            return
        }

        isLoading = true
        rxBus.send(ShowProgressDialogEvent())

        registerDisposable(setUpHatenaIdUsecase.execute(userId.get()).subscribeAsync({
            idErrorMessage.set("")
            rxBus.send(UpdateHatenaIdEvent(userId.get()))
            rxBus.send(DismissEditHatenaIdDialogEvent())
        }, {
            when (it) {
                is HatenaUnAuthorizedException -> {
                    idErrorMessage.set(view.context.getString(R.string.message_error_input_user_id))
                }
                else -> {
                    rxBus.send(FailToConnectionEvent())
                }
            }
        }, {
            isLoading = false
            rxBus.send(DismissProgressDialogEvent())
        }))
    }

    fun onClickCancel(view: View) {
        rxBus.send(DismissEditHatenaIdDialogEvent())
    }
}
