package me.rei_m.hbfavmaterial.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.hideKeyBoard
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.UserModel
import rx.Subscription

/**
 * アプリの初期処理を行うFragment.
 */
public class InitializeFragment : Fragment(), ProgressDialogI {

    override var mProgressDialog: ProgressDialog? = null

    private var mLayoutUserId: TextInputLayout? = null

    private var mSubscription: Subscription? = null

    companion object {
        public fun newInstance(): InitializeFragment {
            return InitializeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_initialize, container, false)

        val editId = view.findViewById(R.id.fragment_initialize_edit_hatena_id) as AppCompatEditText

        val buttonSetId = view.findViewById(R.id.fragment_initialize_button_set_hatena_id) as AppCompatButton

        mLayoutUserId = view.findViewById(R.id.fragment_initialize_layout_hatena_id) as TextInputLayout

        mSubscription = RxTextView.textChanges(editId)
                .map { v -> 0 < v.length }
                .subscribe { isEnabled -> buttonSetId.isEnabled = isEnabled }

        buttonSetId.setOnClickListener { v ->
            val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel
            userModel.checkAndSaveUserId(getAppContext(), editId.editableText.toString())
            showProgressDialog(activity)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        // ユーザー情報が設定済かチェックする
        val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel
        if (userModel.isSetUserSetting()) {
            EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.OK))
        }
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    @Subscribe
    public fun subscribe(event: UserIdCheckedEvent) {

        closeProgressDialog()

        when (event.type) {
            UserIdCheckedEvent.Companion.Type.OK -> {
                // 何もしない。Activity側に処理を委ねる
            }

            UserIdCheckedEvent.Companion.Type.NG -> {
                mLayoutUserId?.error = getString(R.string.message_error_input_user_id)
            }

            UserIdCheckedEvent.Companion.Type.ERROR -> {
                (activity as AppCompatActivity).apply {
                    hideKeyBoard(view)
                    showSnackbarNetworkError(view)
                }
            }
        }
    }
}