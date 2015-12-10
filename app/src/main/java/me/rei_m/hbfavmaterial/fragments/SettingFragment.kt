package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.UserModel

public class SettingFragment : Fragment() {
    companion object {
        public fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val textUserId = view.findViewById(R.id.text_user_id) as AppCompatTextView
        textUserId.text = userModel.userEntity?.id

        val dialog = EditUserIdDialogFragment.newInstance()

        val layoutUserId = view.findViewById(R.id.layout_text_hatena_id) as LinearLayout
        layoutUserId.setOnClickListener({ v ->
            dialog.show(childFragmentManager, EditUserIdDialogFragment.TAG)
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

    @Subscribe
    public fun onUserIdChecked(event: UserIdCheckedEvent) {
        if ( event.type == UserIdCheckedEvent.Companion.Type.OK) {
            val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel
            val textUserId = view.findViewById(R.id.text_user_id) as AppCompatTextView
            textUserId.text = userModel.userEntity?.id
            (getAppContext() as App).resetBookmarks()
        }
    }
}