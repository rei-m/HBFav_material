package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import com.jakewharton.rxbinding.widget.RxTextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activitiy.BaseActivity
import me.rei_m.hbfavmaterial.extensions.adjustScreenWidth
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.fragments.presenter.EditUserIdDialogContact
import me.rei_m.hbfavmaterial.fragments.presenter.EditUserIdDialogPresenter
import me.rei_m.hbfavmaterial.repositories.UserRepository
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class EditUserIdDialogFragment() : DialogFragment(),
        EditUserIdDialogContact.View,
        ProgressDialogController {

    companion object {

        val TAG: String = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance(): EditUserIdDialogFragment = EditUserIdDialogFragment()
    }

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var presenter: EditUserIdDialogPresenter

    override var progressDialog: ProgressDialog? = null

    private var subscription: CompositeSubscription? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EditUserIdDialogPresenter(this)
        val component = (activity as BaseActivity).component
        component.inject(this)
        component.inject(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        subscription = CompositeSubscription()

        val view = inflater.inflate(R.layout.dialog_fragment_edit_user_id, container, false)

        val userEntity = userRepository.resolve()

        with(view.findViewById(R.id.dialog_fragment_edit_user_id_text_title)) {
            this as AppCompatTextView
            text = getString(R.string.dialog_title_set_user)
        }

        val editUserId = view.findViewById(R.id.dialog_fragment_edit_user_id_edit_user_id) as EditText
        editUserId.setText(userEntity.id)

        val buttonCancel = view.findViewById(R.id.dialog_fragment_edit_user_id_button_cancel) as AppCompatButton
        buttonCancel.setOnClickListener { v ->
            dismiss()
        }

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_user_id_button_ok) as AppCompatButton
        buttonOk.setOnClickListener { v ->
            val inputtedUserId = editUserId.editableText.toString()
            if (!userEntity.isSameId(inputtedUserId)) {
                presenter.confirmExistingUserId(inputtedUserId)?.let {
                    subscription?.add(it)
                }
            } else {
                dismiss()
            }
        }

        subscription?.add(RxTextView.textChanges(editUserId)
                .map { v -> 0 < v.length }
                .subscribe { isEnabled -> buttonOk.toggle(isEnabled) })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    override fun showNetworkErrorMessage() {
        (activity as AppCompatActivity).showSnackbarNetworkError(view)
    }

    override fun showProgress() {
        showProgressDialog(activity)
    }

    override fun hideProgress() {
        closeProgressDialog()
    }

    override fun displayInvalidUserIdMessage() {
        view?.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user)?.let {
            it as TextInputLayout
            it.error = getString(R.string.message_error_input_user_id)
        }
    }

    override fun dismissDialog() {
        view?.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user)?.let {
            it as TextInputLayout
            it.isErrorEnabled = false
        }
        dismiss()
    }
}
