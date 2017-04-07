package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditUserIdBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.DismissEditHatenaIdDialogEvent
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditUserIdDialogFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditUserIdDialogFragmentModule
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditUserIdDialogFragmentViewModel
import javax.inject.Inject

class EditUserIdDialogFragment : DialogFragment() {

    companion object {

        val TAG: String = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance() = EditUserIdDialogFragment()
    }

    @Inject
    lateinit var viewModel: EditUserIdDialogFragmentViewModel

    @Inject
    lateinit var rxBus: RxBus

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

        this.binding = binding
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        disposable = CompositeDisposable()
        disposable?.add(rxBus.toObservable().subscribeBus({
            when (it) {
                is DismissEditHatenaIdDialogEvent -> {
                    dismiss()
                }
                is FailToConnectionEvent -> {
                    showFailToConnectionMessage()
                }
            }
        }))
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
        disposable?.dispose()
        disposable = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    private fun showFailToConnectionMessage() {
        binding?.root?.let {
            Snackbar.make(it, getString(R.string.message_error_network), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
        }
    }

    interface Injector {
        fun plus(fragmentModule: EditUserIdDialogFragmentModule?): EditUserIdDialogFragmentComponent
    }
}
