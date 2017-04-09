package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditBookmarkBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditBookmarkDialogFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.EditBookmarkDialogFragmentModule
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditBookmarkDialogFragmentViewModel
import javax.inject.Inject

class EditBookmarkDialogFragment : DialogFragment() {

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

    private var binding: DialogFragmentEditBookmarkBinding? = null

    private var disposable: CompositeDisposable? = null

    private val articleTitle: String by lazy {
        arguments.getString(ARG_ARTICLE_TITLE)
    }

    private val articleUrl: String by lazy {
        arguments.getString(ARG_ARTICLE_URL)
    }

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

        val binding = DialogFragmentEditBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.onCreateView(SnackbarFactory(binding.root))

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
        fun plus(fragmentModule: EditBookmarkDialogFragmentModule?): EditBookmarkDialogFragmentComponent
    }
}
