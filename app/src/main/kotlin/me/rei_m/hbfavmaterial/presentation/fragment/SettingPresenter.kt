package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.twitter.sdk.android.core.TwitterAuthConfig
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.AuthorizeTwitterUsecase
import me.rei_m.hbfavmaterial.usecase.GetHatenaTokenUsecase
import me.rei_m.hbfavmaterial.usecase.GetTwitterSessionUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase

class SettingPresenter(private val getUserUsecase: GetUserUsecase,
                       private val getHatenaTokenUsecase: GetHatenaTokenUsecase,
                       private val getTwitterSessionUsecase: GetTwitterSessionUsecase,
                       private val authorizeTwitterUsecase: AuthorizeTwitterUsecase) : SettingContact.Actions {

    private lateinit var view: SettingContact.View

    private var isLoading = false

    override fun onCreate(view: SettingContact.View) {
        this.view = view
    }

    override fun onViewCreated() {

        view.setUserId(getUserUsecase.get().id)

        view.setHatenaAuthoriseStatus(getHatenaTokenUsecase.get().isAuthorised)
    }

    override fun onResume() {
        view.setTwitterAuthoriseStatus(getTwitterSessionUsecase.get().oAuthTokenEntity.isAuthorised)
    }

    override fun onPause() {
        isLoading = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE -> {
                // TwitterOAuth認可後の処理を行う.
                authorizeTwitterUsecase.onActivityResult(requestCode, resultCode, data)
                return
            }
            ActivityNavigator.REQ_CODE_OAUTH -> {

                data ?: return

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

    override fun onClickTwitterAuthorize(activity: Activity) {
        if (isLoading) return
        isLoading = true
        authorizeTwitterUsecase.authorize(activity)
    }
    
    override fun onDismissEditUserIdDialog() {
        view.updateUserId(getUserUsecase.get().id)
    }
}
