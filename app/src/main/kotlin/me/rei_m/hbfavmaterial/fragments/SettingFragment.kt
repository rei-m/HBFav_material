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
import me.rei_m.hbfavmaterial.activities.OAuthActivity
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.models.TwitterModel
import me.rei_m.hbfavmaterial.repositories.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repositories.UserRepository
import me.rei_m.hbfavmaterial.utils.ConstantUtil
import javax.inject.Inject

/**
 * ユーザーの設定を行うFragment.
 */
class SettingFragment() : BaseFragment() {

    companion object {

        val TAG: String = SettingFragment::class.java.simpleName

        fun newInstance(): SettingFragment = SettingFragment()
    }

    private var listener: OnFragmentInteractionListener? = null

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Inject
    lateinit var twitterModel: TwitterModel

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
            val dialog = EditUserIdDialogFragment.newInstance()
            dialog.onDismiss(object : DialogInterface {
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
            })
            dialog.show(childFragmentManager, EditUserIdDialogFragment.TAG)
        }

        val oauthTextId = if (hatenaTokenRepository.resolve().isAuthorised)
            R.string.text_hatena_account_connect_ok else
            R.string.text_hatena_account_connect_no

        with(view.findViewById(R.id.fragment_setting_text_user_oauth)) {
            this as AppCompatTextView
            text = resources.getString(oauthTextId)
        }

        view.findViewById(R.id.fragment_setting_layout_text_hatena_oauth).setOnClickListener {
            startActivityForResult(OAuthActivity.createIntent(activity), ConstantUtil.REQ_CODE_OAUTH)
        }

        view.findViewById(R.id.fragment_setting_layout_text_twitter_oauth).setOnClickListener {
            twitterModel.authorize(activity)
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val view = view ?: return

        val textTwitterOAuth = view.findViewById(R.id.fragment_setting_text_twitter_oauth) as AppCompatTextView
        val twitterOAuthTextId = if (twitterModel.isAuthorised())
            R.string.text_hatena_account_connect_ok else
            R.string.text_hatena_account_connect_no

        textTwitterOAuth.text = resources.getString(twitterOAuthTextId)
    }

    override fun onPause() {
        super.onPause()
        twitterModel.clearBusy()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data ?: return

        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            // TwitterOAuth認可後の処理を行う.
            twitterModel.onActivityResult(requestCode, resultCode, data)
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
            else -> {

            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onUserIdUpdated(userId: String)
    }
}
