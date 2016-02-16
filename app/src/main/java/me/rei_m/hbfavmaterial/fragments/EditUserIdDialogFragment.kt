package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditUserIdBinding
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.adjustScreenWidth
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.UserModel
import rx.Subscription
import javax.inject.Inject

class EditUserIdDialogFragment : DialogFragment(), IProgressDialog {

    @Inject
    lateinit var userModel: UserModel

    override var mProgressDialog: ProgressDialog? = null

    lateinit private var mSubscription: Subscription

    companion object {

        final val TAG = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance(): EditUserIdDialogFragment {
            return EditUserIdDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DialogFragmentEditUserIdBinding.inflate(inflater, container, false)
        
        binding.dialogFragmentEditUserIdTextTitle.text = getString(R.string.dialog_title_set_user)

        binding.dialogFragmentEditUserIdEditUserId.setText(userModel.userEntity?.id)

        binding.dialogFragmentEditUserIdButtonCancel.setOnClickListener {
            dismiss()
        }

        binding.dialogFragmentEditUserIdButtonOk.setOnClickListener {
            val inputtedUserId = binding.dialogFragmentEditUserIdEditUserId.editableText.toString()
            if (inputtedUserId != userModel.userEntity?.id) {
                userModel.checkAndSaveUserId(getAppContext(), binding.dialogFragmentEditUserIdEditUserId.editableText.toString())
                showProgressDialog(activity)
            } else {
                dismiss()
            }
        }

        mSubscription = RxTextView.textChanges(binding.dialogFragmentEditUserIdEditUserId)
                .map { v ->
                    0 < v.length
                }
                .subscribe { isEnabled ->
                    binding.dialogFragmentEditUserIdButtonOk.toggle(isEnabled)
                }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription.unsubscribe()
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    @Subscribe
    fun subscribe(event: UserIdCheckedEvent) {

        val binding = DataBindingUtil.getBinding<DialogFragmentEditUserIdBinding>(view)

        closeProgressDialog()

        when (event.type) {
            UserIdCheckedEvent.Companion.Type.OK -> {
                binding.dialogFragmentEditUserIdLayoutEditUser.isErrorEnabled = false
                dismiss()
            }

            UserIdCheckedEvent.Companion.Type.NG -> {
                binding.dialogFragmentEditUserIdLayoutEditUser.error = getString(R.string.message_error_input_user_id)
            }

            UserIdCheckedEvent.Companion.Type.ERROR -> {
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
        }
    }
}
