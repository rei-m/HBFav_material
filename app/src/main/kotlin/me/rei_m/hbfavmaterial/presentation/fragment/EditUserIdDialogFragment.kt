package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Dialog
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
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditUserIdBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditUserIdDialogFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.EditUserIdDialogFragmentViewModelModule
import javax.inject.Inject

class EditUserIdDialogFragment : DialogFragment() {

    companion object {

        val TAG: String = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance() = EditUserIdDialogFragment()
    }

    @Inject
    lateinit var viewModel: EditUserIdDialogFragmentViewModel

    private var binding: DialogFragmentEditUserIdBinding? = null

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DialogFragmentEditUserIdBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.onCreateView(SnackbarFactory(binding.root))

        this.binding = binding
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.dismissDialogEvent.subscribe {
            dismiss()
        })
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
        disposable?.dispose()
        disposable = null
    }

    override fun onDestroyView() {
        viewModel.onDestroyView()
        binding = null
        super.onDestroyView()
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
