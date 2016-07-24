package me.rei_m.hbfavmaterial.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activities.MainActivity
import me.rei_m.hbfavmaterial.entities.UserEntity
import me.rei_m.hbfavmaterial.extensions.hideKeyBoard
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.repositories.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import retrofit2.adapter.rxjava.HttpException
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * アプリの初期処理を行うFragment.
 */
class InitializeFragment() : BaseFragment(), ProgressDialogController {

    companion object {
        fun newInstance(): InitializeFragment = InitializeFragment()
    }

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userService: UserService

    override var progressDialog: ProgressDialog? = null

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_initialize, container, false)

        val editId = view.findViewById(R.id.fragment_initialize_edit_hatena_id) as AppCompatEditText

        val buttonSetId = view.findViewById(R.id.fragment_initialize_button_set_hatena_id) as AppCompatButton
        buttonSetId.setOnClickListener { confirmAndSaveUserId(editId.editableText.toString()) }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onResume() {
        super.onResume()
        subscription = CompositeSubscription()
        isLoading = false

        val view = view ?: return

        val editId = view.findViewById(R.id.fragment_initialize_edit_hatena_id) as AppCompatEditText

        val buttonSetId = view.findViewById(R.id.fragment_initialize_button_set_hatena_id) as AppCompatButton

        subscription?.add(RxTextView.textChanges(editId)
                .map { v -> 0 < v.length }
                .subscribe { isEnabled -> buttonSetId.isEnabled = isEnabled })

        val userEntity = userRepository.resolve()
        if (userEntity.isCompleteSetting) {
            activity.startActivityWithClearTop(MainActivity.createIntent(activity))
        }
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
        subscription = null
    }

    private fun confirmAndSaveUserId(userId: String) {

        if (isLoading) return

        isLoading = true

        showProgressDialog(activity)

        val observer = object : Observer<Boolean> {

            override fun onNext(t: Boolean) {
                if (t) {
                    userRepository.store(context, UserEntity(userId))
                    activity.startActivityWithClearTop(MainActivity.createIntent(activity))
                } else {
                    view?.findViewById(R.id.fragment_initialize_layout_hatena_id)?.let {
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
                        view?.findViewById(R.id.fragment_initialize_layout_hatena_id)?.let {
                            it as TextInputLayout
                            it.error = getString(R.string.message_error_input_user_id)
                        }
                        return
                    }
                }
                with(activity as AppCompatActivity) {
                    hideKeyBoard(view)
                    showSnackbarNetworkError(view)
                }
            }
        }

        userService.confirmExistingUserId(userId)
                .doOnUnsubscribe {
                    isLoading = false
                    closeProgressDialog()
                }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}
