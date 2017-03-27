package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.InitializeFragmentComponent
import me.rei_m.hbfavmaterial.di.InitializeFragmentModule
import me.rei_m.hbfavmaterial.extension.hideKeyBoard
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import javax.inject.Inject

/**
 * アプリの初期処理を行うFragment.
 */
class InitializeFragment : BaseFragment(),
        InitializeContact.View,
        ProgressDialogController {

    companion object {
        fun newInstance(): InitializeFragment = InitializeFragment()
    }

    @Inject
    lateinit var navigator: ActivityNavigator

    @Inject
    lateinit var presenter: InitializeContact.Actions

    override var progressDialog: ProgressDialog? = null

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        disposable = CompositeDisposable()

        val view = inflater.inflate(R.layout.fragment_initialize, container, false)

        val editId = view.findViewById(R.id.fragment_initialize_edit_hatena_id) as AppCompatEditText

        val buttonSetId = view.findViewById(R.id.fragment_initialize_button_set_hatena_id) as AppCompatButton
        buttonSetId.setOnClickListener {
            presenter.onClickButtonSetId(editId.editableText.toString())
        }

        disposable?.add(RxTextView.textChanges(editId)
                .map { v -> v.isNotEmpty() }
                .subscribe { isEnabled -> buttonSetId.isEnabled = isEnabled })

        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
        disposable = null
    }

    override fun showNetworkErrorMessage() {
        with(activity as AppCompatActivity) {
            hideKeyBoard(activity.window.decorView)
            showSnackbarNetworkError()
        }
    }

    override fun showProgress() {
        showProgressDialog(activity)
    }

    override fun hideProgress() {
        closeProgressDialog()
    }

    override fun displayInvalidUserIdMessage() {
        view?.findViewById(R.id.fragment_initialize_layout_hatena_id)?.let {
            it as TextInputLayout
            it.error = getString(R.string.message_error_input_user_id)
        }
    }

    override fun navigateToMain() {
        navigator.navigateToMain()
        activity.finish()
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .plus(InitializeFragmentModule(context))
                .inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: InitializeFragmentModule?): InitializeFragmentComponent
    }
}
