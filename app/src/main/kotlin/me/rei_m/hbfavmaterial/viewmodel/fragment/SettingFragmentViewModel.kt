package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.ShowEditHatenaIdDialogEvent
import me.rei_m.hbfavmaterial.presentation.event.StartAuthoriseTwitterEvent
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class SettingFragmentViewModel(private val userModel: UserModel,
                               private val hatenaService: HatenaService,
                               private val twitterService: TwitterService,
                               private val rxBus: RxBus,
                               private val navigator: Navigator) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val isAuthorisedHatena: ObservableBoolean = ObservableBoolean(false)

    val isAuthorisedTwitter: ObservableBoolean = ObservableBoolean(false)

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.userUpdatedEvent.subscribe {
            userId.set(it.id)
        }, hatenaService.confirmAuthorisedEvent.subscribe {
            isAuthorisedHatena.set(it)
        }, twitterService.confirmAuthorisedEvent.subscribe {
            isAuthorisedTwitter.set(it)
        })
    }

    override fun onResume() {
        super.onResume()
        userId.set(userModel.user.id)
        hatenaService.confirmAuthorised()
        twitterService.confirmAuthorised()
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
