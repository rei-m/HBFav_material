package me.rei_m.hbfavmaterial.fragments.presenter

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.twitter.sdk.android.core.TwitterAuthConfig
import me.rei_m.hbfavmaterial.activitiy.OAuthActivity
import me.rei_m.hbfavmaterial.fragments.BaseFragment
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repositories.TwitterSessionRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository
import me.rei_m.hbfavmaterial.service.TwitterService
import javax.inject.Inject

class SettingPresenter(val view: SettingContact.View) : SettingContact.Actions {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var twitterSessionRepository: TwitterSessionRepository

    @Inject
    lateinit var twitterService: TwitterService

    private var isLoading = false

    init {
        (view as BaseFragment).component.inject(this)
    }

    override fun clickTwitterOAuth(activity: Activity) {
        if (isLoading) return
        isLoading = true
        twitterService.authorize(activity)
    }

    override fun onViewCreated() {

        view.setUserId(userRepository.resolve().id)

        view.setHatenaAuthoriseStatus(hatenaTokenRepository.resolve().isAuthorised)
    }

    override fun onResume() {

        view.setTwitterAuthoriseStatus(twitterSessionRepository.resolve().oAuthTokenEntity.isAuthorised)
    }

    override fun onPause() {
        isLoading = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        isLoading = false

        data ?: return

        when (requestCode) {
            TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE -> {
                // TwitterOAuth認可後の処理を行う.
                twitterService.onActivityResult(requestCode, resultCode, data)
                return
            }
            ActivityNavigator.REQ_CODE_OAUTH -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    // 認可の可否が選択されたかチェック
                    if (data.extras.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)) {
                        // 認可の結果により表示を更新する.
                        view.setHatenaAuthoriseStatus(data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS))
                    } else {
                        // 認可を選択せずにresultCodeが設定された場合はネットワークエラーのケース.
                        view.showNetworkErrorMessage()
                    }
                }
                return
            }
        }
    }

    override fun onDismissEditUserIdDialog() {
        view.updateUserId(userRepository.resolve().id)
    }
}
