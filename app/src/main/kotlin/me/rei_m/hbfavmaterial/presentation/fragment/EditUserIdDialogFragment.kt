package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditUserIdBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditUserIdDialogFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditUserIdDialogFragmentModule
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditUserIdDialogFragmentViewModel
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

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as HasComponent<Injector>).getComponent()
                .plus(EditUserIdDialogFragmentModule())
                .inject(this)
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

    interface Injector {
        fun plus(fragmentModule: EditUserIdDialogFragmentModule?): EditUserIdDialogFragmentComponent
    }
}
