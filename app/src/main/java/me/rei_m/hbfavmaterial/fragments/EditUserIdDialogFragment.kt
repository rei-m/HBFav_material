package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.widget.EditText
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.UserModel
import rx.Subscription

public class EditUserIdDialogFragment : DialogFragment(), ProgressDialogI {

    override var mProgressDialog: ProgressDialog? = null

    private var mLayoutUserId: TextInputLayout? = null

    private var mSubscription: Subscription? = null

    companion object {

        public fun newInstance(): EditUserIdDialogFragment {
            return EditUserIdDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {

        val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_fragment_edit_user_id, null)

        val editUserId = view.findViewById(R.id.dialog_fragment_edit_user_id_edit_user_id) as EditText
        editUserId.setText(userModel.userEntity?.id)

        mLayoutUserId = view.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user) as TextInputLayout

        val buttonCancel = view.findViewById(R.id.dialog_fragment_edit_user_id_button_cancel) as AppCompatButton
        buttonCancel.setOnClickListener({ v ->
            dismiss()
        })

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_user_id_button_ok) as AppCompatButton
        buttonOk.setOnClickListener({ v ->
            val inputtedUserId = editUserId.editableText.toString()
            if (inputtedUserId != userModel.userEntity?.id) {
                userModel.checkAndSaveUserId(getAppContext(), editUserId.editableText.toString())
                showProgressDialog(activity)
            } else {
                dismiss()
            }
        })

        val editUserIdStream = RxTextView.textChanges(editUserId)
        mSubscription = editUserIdStream
                .map({ v -> 0 < v.length })
                .subscribe({ isEnabled -> buttonOk.toggle(isEnabled) })

        val builder = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.dialog_title_set_user))
                .setView(view)

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
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
    @SuppressWarnings("unused")
    public fun onUserIdChecked(event: UserIdCheckedEvent) {

        closeProgressDialog()

        when (event.type) {
            UserIdCheckedEvent.Companion.Type.OK -> {
                mLayoutUserId?.isErrorEnabled = false
                dismiss()
            }

            UserIdCheckedEvent.Companion.Type.NG -> {
                mLayoutUserId?.error = getString(R.string.message_error_input_user_id)
            }

            UserIdCheckedEvent.Companion.Type.ERROR -> {
                mLayoutUserId?.error = getString(R.string.message_error_network)
            }
        }
    }
}