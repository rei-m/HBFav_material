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
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.fragments.presenter.SettingContact
import me.rei_m.hbfavmaterial.fragments.presenter.SettingPresenter
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import javax.inject.Inject

/**
 * ユーザーの設定を行うFragment.
 */
class SettingFragment() : BaseFragment(),
        SettingContact.View,
        DialogInterface {

    companion object {

        val TAG: String = SettingFragment::class.java.simpleName

        fun newInstance(): SettingFragment = SettingFragment()
    }

    @Inject
    lateinit var navigator: ActivityNavigator

    lateinit var presenter: SettingPresenter

    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        presenter = SettingPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        view.findViewById(R.id.fragment_setting_layout_text_hatena_id).setOnClickListener {
            EditUserIdDialogFragment.newInstance().apply {
                setTargetFragment(this@SettingFragment, 0)
            }.let {
                it.show(childFragmentManager, EditUserIdDialogFragment.TAG)
            }
        }

        view.findViewById(R.id.fragment_setting_layout_text_hatena_oauth).setOnClickListener {
            navigator.navigateToOAuth(activity)
        }

        view.findViewById(R.id.fragment_setting_layout_text_twitter_oauth).setOnClickListener {
            presenter.clickTwitterOAuth(activity)
        }

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun dismiss() {
        presenter.onDismissEditUserIdDialog()
    }

    override fun cancel() {

    }

    override fun setUserId(userId: String) {
        view?.findViewById(R.id.fragment_setting_text_user_id).let {
            it as AppCompatTextView
            it.text = userId
        }
    }

    override fun updateUserId(userId: String) {
        setUserId(userId)
        listener?.onUserIdUpdated(userId)
    }

    override fun setHatenaAuthoriseStatus(isAuthorised: Boolean) {
        view?.findViewById(R.id.fragment_setting_text_user_oauth).let {
            it as AppCompatTextView
            val oauthTextId = if (isAuthorised)
                R.string.text_hatena_account_connect_ok
            else
                R.string.text_hatena_account_connect_no
            it.text = resources.getString(oauthTextId)
        }
    }

    override fun setTwitterAuthoriseStatus(isAuthorised: Boolean) {
        view?.findViewById(R.id.fragment_setting_text_twitter_oauth).let {
            it as AppCompatTextView
            val twitterOAuthTextId = if (isAuthorised)
                R.string.text_hatena_account_connect_ok
            else
                R.string.text_hatena_account_connect_no
            it.text = resources.getString(twitterOAuthTextId)
        }
    }

    override fun showNetworkErrorMessage() {
        (activity as AppCompatActivity).showSnackbarNetworkError(view)
    }

    interface OnFragmentInteractionListener {
        fun onUserIdUpdated(userId: String)
    }
}
