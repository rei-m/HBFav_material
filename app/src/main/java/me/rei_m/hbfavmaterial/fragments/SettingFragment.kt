package me.rei_m.hbfavmaterial.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.otto.Subscribe
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activities.OAuthActivity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdChangedEvent
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HatenaModel
import me.rei_m.hbfavmaterial.models.TwitterModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.utils.ConstantUtil

/**
 * ユーザーの設定を行うFragment.
 */
public class SettingFragment : Fragment() {

    companion object {

        public val TAG = SettingFragment::class.java.simpleName

        public fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val textUserId = view.findViewById(R.id.fragment_setting_text_user_id) as AppCompatTextView
        textUserId.text = userModel.userEntity?.id

        val layoutUserId = view.findViewById(R.id.fragment_setting_layout_text_hatena_id) as LinearLayout
        layoutUserId.setOnClickListener { v ->
            EditUserIdDialogFragment
                    .newInstance()
                    .show(childFragmentManager, EditUserIdDialogFragment.TAG)
        }

        val hatenaModel = ModelLocator.get(ModelLocator.Companion.Tag.HATENA) as HatenaModel

        val textHatenaOAuth = view.findViewById(R.id.fragment_setting_text_user_oauth) as AppCompatTextView
        val oauthTextId = if (hatenaModel.isAuthorised())
            R.string.text_hatena_account_connect_ok else
            R.string.text_hatena_account_connect_no
        textHatenaOAuth.text = resources.getString(oauthTextId)

        val layoutHatenaOAuth = view.findViewById(R.id.fragment_setting_layout_text_hatena_oauth) as LinearLayout
        layoutHatenaOAuth.setOnClickListener { v ->
            startActivityForResult(OAuthActivity.createIntent(activity), ConstantUtil.REQ_CODE_OAUTH)
        }

        val buttonTwitterLogin = view.findViewById(R.id.login_button) as TwitterLoginButton
        val context = getAppContext()
        buttonTwitterLogin.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                result ?: return
                val twitterModel = ModelLocator.get(ModelLocator.Companion.Tag.TWITTER) as TwitterModel
                twitterModel.saveSession(context, result.data)
            }

            override fun failure(exception: TwitterException?) {

            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data ?: return

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            // Fabric SDKからのTwitter認可後の処理を行う.
            val buttonTwitterLogin = view.findViewById(R.id.login_button) as TwitterLoginButton
            buttonTwitterLogin.onActivityResult(requestCode, resultCode, data);
            return
        }

        // はてなのOAuth以外のリクエストの場合は終了.
        if (requestCode != ConstantUtil.REQ_CODE_OAUTH) {
            return
        }

        // OAuthの認可後の処理を行う.
        when (resultCode) {
            AppCompatActivity.RESULT_OK -> {
                // 認可の可否が選択されたかチェック
                if (data.extras.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)) {
                    // 認可の結果により表示を更新する.
                    val textHatenaOAuth = view.findViewById(R.id.fragment_setting_text_user_oauth) as AppCompatTextView
                    val oauthTextId = if (data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS))
                        R.string.text_hatena_account_connect_ok else
                        R.string.text_hatena_account_connect_no
                    textHatenaOAuth.text = resources.getString(oauthTextId)
                } else {
                    // 認可を選択せずにresultCodeが設定された場合はネットワークエラーのケース.
                    (activity as AppCompatActivity).showSnackbarNetworkError(view)
                }
            }
            else -> {

            }
        }
    }

    /**
     * ユーザーIDチェック時のイベント.
     */
    @Subscribe
    public fun subscribe(event: UserIdCheckedEvent) {
        if (event.type == UserIdCheckedEvent.Companion.Type.OK) {
            val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel
            userModel.userEntity?.apply {
                val textUserId = view.findViewById(R.id.fragment_setting_text_user_id) as AppCompatTextView
                textUserId.text = id
                EventBusHolder.EVENT_BUS.post(UserIdChangedEvent(id))
            }
        }
    }
}