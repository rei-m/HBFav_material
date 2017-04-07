package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditBookmarkBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditBookmarkDialogFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditBookmarkDialogFragmentModule
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditBookmarkDialogFragmentViewModel
import javax.inject.Inject

class EditBookmarkDialogFragment : DialogFragment(),
        ProgressDialogController {

    companion object {

        val TAG: String = EditBookmarkDialogFragment::class.java.simpleName

        private const val ARG_ARTICLE_TITLE = "ARG_ARTICLE_TITLE"

        private const val ARG_ARTICLE_URL = "ARG_ARTICLE_URL"

        fun newInstance(title: String,
                        url: String) = EditBookmarkDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ARTICLE_TITLE, title)
                putString(ARG_ARTICLE_URL, url)
            }
        }
    }

    @Inject
    lateinit var viewModel: EditBookmarkDialogFragmentViewModel

    @Inject
    lateinit var rxBus: RxBus

    private var binding: DialogFragmentEditBookmarkBinding? = null

    private var disposable: CompositeDisposable? = null

    private val articleTitle: String by lazy {
        arguments.getString(ARG_ARTICLE_TITLE)
    }

    private val articleUrl: String by lazy {
        arguments.getString(ARG_ARTICLE_URL)
    }

    override var progressDialog: ProgressDialog? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as HasComponent<Injector>).getComponent()
                .plus(EditBookmarkDialogFragmentModule())
                .inject(this)

        viewModel.onCreate(articleTitle, articleUrl)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        disposable = CompositeDisposable()

        val binding = DialogFragmentEditBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

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
                is ShowProgressDialogEvent -> {
                    showProgressDialog(activity)
                }
                is DismissProgressDialogEvent -> {
                    closeProgressDialog()
                }
                is DismissEditBookmarkDialogEvent -> {
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
        fun plus(fragmentModule: EditBookmarkDialogFragmentModule?): EditBookmarkDialogFragmentComponent
    }
}
