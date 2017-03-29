package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.DisplaySettingUsecase

class SettingFragmentViewModel(private val displaySettingUsecase: DisplaySettingUsecase,
                               private val rxBus: RxBus,
                               private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val isAuthorisedHatena: ObservableBoolean = ObservableBoolean(false)

    val isAuthorisedTwitter: ObservableBoolean = ObservableBoolean(false)

    override fun onResume() {
        super.onResume()
        registerDisposable(displaySettingUsecase.execute().subscribeAsync({
            userId.set(it.first)
            isAuthorisedHatena.set(it.second)
            isAuthorisedTwitter.set(it.third)
        }), rxBus.toObservable().subscribeBus({
            when (it) {
                is UpdateHatenaIdEvent -> {
                    userId.set(it.userId)
                }
            }
        }))
    }

    fun onClickHatenaId(view: View) {
        rxBus.send(ShowEditHatenaIdDialogEvent())
    }

    fun onClickHatenaAuthStatus(view: View) {
        navigator.navigateToOAuth()
    }

    fun onAuthoriseHatena(isDone: Boolean, isAuthorise: Boolean) {
        if (isDone) {
            isAuthorisedHatena.set(isAuthorise)
        } else {
            // 認可を選択せずにresultCodeが設定された場合はネットワークエラーのケース.
            rxBus.send(FailToConnectionEvent())
        }
    }

    fun onClickTwitterAuthStatus(view: View) {
        rxBus.send(StartAuthoriseTwitterEvent())
    }
}
