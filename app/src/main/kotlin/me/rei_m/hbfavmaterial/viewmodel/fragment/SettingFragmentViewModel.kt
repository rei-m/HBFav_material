package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class SettingFragmentViewModel(private val userModel: UserModel,
                               private val hatenaService: HatenaService,
                               private val twitterService: TwitterService,
                               private val navigator: Navigator) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val isAuthorisedHatena: ObservableBoolean = ObservableBoolean(false)

    val isAuthorisedTwitter: ObservableBoolean = ObservableBoolean(false)

    private var showEditHatenaIdDialogEventSubject = PublishSubject.create<Unit>()
    val showEditHatenaIdDialogEvent: Observable<Unit> = showEditHatenaIdDialogEventSubject

    private var startAuthoriseTwitterEventSubject = PublishSubject.create<Unit>()
    val startAuthoriseTwitterEvent: Observable<Unit> = startAuthoriseTwitterEventSubject

    private var snackbarFactory: SnackbarFactory? = null

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

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

    fun onDestroyView() {
        snackbarFactory = null
    }

    fun onClickHatenaId(view: View) {
        showEditHatenaIdDialogEventSubject.onNext(Unit)
    }

    fun onClickHatenaAuthStatus(view: View) {
        navigator.navigateToOAuth()
    }

    fun onAuthoriseHatena(isDone: Boolean, isAuthorise: Boolean) {
        if (isDone) {
            isAuthorisedHatena.set(isAuthorise)
        } else {
            // 認可を選択せずにresultCodeが設定された場合はネットワークエラーのケース.
            snackbarFactory?.create(R.string.message_error_network)?.show()
        }
    }

    fun onClickTwitterAuthStatus(view: View) {
        startAuthoriseTwitterEventSubject.onNext(Unit)
    }
}
