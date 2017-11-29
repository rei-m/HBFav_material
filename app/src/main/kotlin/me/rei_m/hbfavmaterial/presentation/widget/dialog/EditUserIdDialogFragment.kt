package me.rei_m.hbfavmaterial.presentation.widget.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditUserIdBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.EditUserIdDialogFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.di.EditUserIdDialogFragmentViewModelModule
import javax.inject.Inject

class EditUserIdDialogFragment : DialogFragment() {

    companion object {

        val TAG: String = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance() = EditUserIdDialogFragment()
    }

    @Inject
    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var viewModelFactory: EditUserIdDialogFragmentViewModel.Factory

    private lateinit var binding: DialogFragmentEditUserIdBinding

    private lateinit var viewModel: EditUserIdDialogFragmentViewModel

    private var disposable: CompositeDisposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditUserIdDialogFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogFragmentEditUserIdBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.dismissDialogEvent.subscribe {
            dismiss()
        }, viewModel.isLoading.subscribe {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }, viewModel.isRaisedError.subscribe {
            SnackbarFactory(binding.root).create(R.string.message_error_network).show()
        })
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(EditUserIdDialogFragmentViewModelModule::class))
        internal abstract fun contributeInjector(): EditUserIdDialogFragment
    }
}
