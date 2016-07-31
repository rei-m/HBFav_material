package me.rei_m.hbfavmaterial.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.twitter.sdk.android.core.TwitterAuthConfig
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activitiy.OAuthActivity
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repositories.TwitterSessionRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository
import me.rei_m.hbfavmaterial.service.TwitterService
import javax.inject.Inject

/**
 * ユーザーの設定を行うFragment.
 */
class SettingFragment() : BaseFragment(), DialogInterface {

    companion object {

        val TAG: String = SettingFragment::class.java.simpleName

        fun newInstance(): SettingFragment = SettingFragment()
    }

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var twitterSessionRepository: TwitterSessionRepository

    @Inject
    lateinit var twitterService: TwitterService

    private var listener: OnFragmentInteractionListener? = null

    private var isLoading = false

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        with(view.findViewById(R.id.fragment_setting_text_user_id)) {
            this as AppCompatTextView
            text = userRepository.resolve().id
        }

        view.findViewById(R.id.fragment_setting_layout_text_hatena_id).setOnClickListener {
            EditUserIdDialogFragment.newInstance().apply {
                setTargetFragment(this@SettingFragment, 0)
            }.let {
                it.show(childFragmentManager, EditUserIdDialogFragment.TAG)
            }
        }

        val oauthTextId = if (hatenaTokenRepository.resolve().isAuthorised)
            R.string.text_hatena_account_connect_ok else
            R.string.text_hatena_account_connect_no

        with(view.findViewById(R.id.fragment_setting_text_user_oauth)) {
            this as AppCompatTextView
            text = resources.getString(oauthTextId)
        }

        view.findViewById(R.id.fragment_setting_layout_text_hatena_oauth).setOnClickListener {
            navigator.navigateToOAuth(activity)
        }

        view.findViewById(R.id.fragment_setting_layout_text_twitter_oauth).setOnClickListener {
            if (isLoading) return@setOnClickListener
            isLoading = true
            twitterService.authorize(activity)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById(R.id.fragment_setting_text_twitter_oauth).let {
            it as AppCompatTextView
            val twitterOAuthTextId = if (twitterSessionRepository.resolve().oAuthTokenEntity.isAuthorised)
                R.string.text_hatena_account_connect_ok else
                R.string.text_hatena_account_connect_no
            it.text = resources.getString(twitterOAuthTextId)
        }
    }

    override fun onPause() {
        super.onPause()
        isLoading = false
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
                        view?.run {
                            val textHatenaOAuth = findViewById(R.id.fragment_setting_text_user_oauth) as AppCompatTextView
                            val oauthTextId = if (data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS))
                                R.string.text_hatena_account_connect_ok else
                                R.string.text_hatena_account_connect_no
                            textHatenaOAuth.text = resources.getString(oauthTextId)
                        }
                    } else {
                        // 認可を選択せずにresultCodeが設定された場合はネットワークエラーのケース.
                        (activity as AppCompatActivity).showSnackbarNetworkError(view)
                    }
                }
                return
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onUserIdUpdated(userId: String)
    }

    override fun dismiss() {
        val userEntity = userRepository.resolve()
        view?.findViewById(R.id.fragment_setting_text_user_id).let {
            it as AppCompatTextView
            it.text = userEntity.id
        }
        listener?.onUserIdUpdated(userEntity.id)
    }

    override fun cancel() {

    }
}
