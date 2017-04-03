package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.exception.HatenaUnAuthorizedException
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.SetUpHatenaIdUsecase
import me.rei_m.hbfavmaterial.usecase.StartApplicationUsecase

class InitializeFragmentViewModel(private val startApplicationUsecase: StartApplicationUsecase,
                                  private val setUpHatenaIdUsecase: SetUpHatenaIdUsecase,
                                  private val rxBus: RxBus,
                                  private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    private var isLoading: Boolean = false

    override fun onResume() {
        super.onResume()
        registerDisposable(startApplicationUsecase.execute().subscribeAsync({
            if (it) {
                navigator.navigateToMain()
                rxBus.send(FinishActivityEvent())
            }
        }))
    }

    fun onClickButtonSetId(view: View) {

        if (isLoading) {
            return
        }

        isLoading = true
        rxBus.send(ShowProgressDialogEvent())

        registerDisposable(setUpHatenaIdUsecase.execute(userId.get()).subscribeAsync({
            navigator.navigateToMain()
            rxBus.send(FinishActivityEvent())
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
}
