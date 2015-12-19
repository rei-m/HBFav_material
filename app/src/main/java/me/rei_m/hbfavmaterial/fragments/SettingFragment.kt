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
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activities.OAuthActivity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserIdChangedEvent
import me.rei_m.hbfavmaterial.events.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HatenaModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.utils.ConstantUtil

public class SettingFragment : Fragment() {

    companion object {
        public fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val textUserId = view.findViewById(R.id.fragment_setting_text_user_id) as AppCompatTextView
        textUserId.text = userModel.userEntity?.id

        val dialog = EditUserIdDialogFragment.newInstance()

        val layoutUserId = view.findViewById(R.id.fragment_setting_layout_text_hatena_id) as LinearLayout
        layoutUserId.setOnClickListener({ v ->
            dialog.show(childFragmentManager, EditUserIdDialogFragment.TAG)
        })

        val hatenaModel = ModelLocator.get(ModelLocator.Companion.Tag.HATENA) as HatenaModel

        val textHatenaOAuth = view.findViewById(R.id.fragment_setting_text_user_oauth) as AppCompatTextView
        val oauthTextId = if (hatenaModel.isAuthorised())
            R.string.text_hatena_account_connect_ok else
            R.string.text_hatena_account_connect_no
        textHatenaOAuth.text = resources.getString(oauthTextId)

        val layoutHatenaOAuth = view.findViewById(R.id.fragment_setting_layout_text_hatena_oauth) as LinearLayout
        layoutHatenaOAuth.setOnClickListener({ v ->
            startActivityForResult(OAuthActivity.createIntent(activity), ConstantUtil.REQ_CODE_OAUTH)
        })

        return view
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != ConstantUtil.REQ_CODE_OAUTH) {
            return
        }

        when (resultCode) {
            AppCompatActivity.RESULT_OK -> {
                val textHatenaOAuth = view.findViewById(R.id.fragment_setting_text_user_oauth) as AppCompatTextView
                val oauthTextId = if (data!!.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS))
                    R.string.text_hatena_account_connect_ok else
                    R.string.text_hatena_account_connect_no
                textHatenaOAuth.text = resources.getString(oauthTextId)
            }
            AppCompatActivity.RESULT_CANCELED -> {
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
            else -> {

            }
        }
    }

    @Subscribe
    public fun onUserIdChecked(event: UserIdCheckedEvent) {
        if (event.type == UserIdCheckedEvent.Companion.Type.OK) {
            val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel
            val textUserId = view.findViewById(R.id.fragment_setting_text_user_id) as AppCompatTextView
            textUserId.text = userModel.userEntity?.id
            (getAppContext() as App).resetBookmarks()
            EventBusHolder.EVENT_BUS.post(UserIdChangedEvent(userModel.userEntity?.id!!))
        }
    }
}