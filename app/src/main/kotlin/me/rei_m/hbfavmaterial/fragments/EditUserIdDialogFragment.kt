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
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.extensions.adjustScreenWidth
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.repositories.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import retrofit2.adapter.rxjava.HttpException
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection
import javax.inject.Inject

class EditUserIdDialogFragment() : DialogFragment(), ProgressDialogController {

    companion object {

        val TAG: String = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance(): EditUserIdDialogFragment = EditUserIdDialogFragment()
    }

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userService: UserService

    override var progressDialog: ProgressDialog? = null

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as BaseActivity).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

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
                confirmAndSaveUserId(inputtedUserId)
            } else {
                dismiss()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        subscription = CompositeSubscription()
        isLoading = false

        val view = view ?: return

        val editUserId = view.findViewById(R.id.dialog_fragment_edit_user_id_edit_user_id) as EditText

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_user_id_button_ok) as AppCompatButton

        subscription?.add(RxTextView.textChanges(editUserId)
                .map { v -> 0 < v.length }
                .subscribe { isEnabled -> buttonOk.toggle(isEnabled) })

    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    private fun confirmAndSaveUserId(userId: String) {

        if (isLoading) return

        isLoading = true

        showProgressDialog(activity)

        val observer = object : Observer<Boolean> {

            override fun onNext(t: Boolean) {
                if (t) {
                    userRepository.store(context, UserEntity(userId))
                    view?.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user)?.let {
                        it as TextInputLayout
                        it.isErrorEnabled = false
                        dismiss()
                    }
                } else {
                    view?.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user)?.let {
                        it as TextInputLayout
                        it.error = getString(R.string.message_error_input_user_id)
                    }
                }
            }

            override fun onCompleted() {
            }

            override fun onError(e: Throwable?) {
                if (e is HttpException) {
                    if (e.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                        view?.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user)?.let {
                            it as TextInputLayout
                            it.error = getString(R.string.message_error_input_user_id)
                        }
                        return
                    }
                }
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
        }

        subscription?.add(userService.confirmExistingUserId(userId)
                .doOnUnsubscribe {
                    isLoading = false
                    closeProgressDialog()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer))
    }
}
