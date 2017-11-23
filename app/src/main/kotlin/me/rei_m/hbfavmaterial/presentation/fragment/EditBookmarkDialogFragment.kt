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
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditBookmarkDialogFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.EditBookmarkDialogFragmentViewModelModule
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

    private val articleTitle: String? by lazy {
        arguments?.getString(ARG_ARTICLE_TITLE)
    }

    private val articleUrl: String? by lazy {
        arguments?.getString(ARG_ARTICLE_URL)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(articleTitle!!, articleUrl!!)
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

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(EditBookmarkDialogFragmentViewModelModule::class))
        internal abstract fun contributeInjector(): EditBookmarkDialogFragment
    }
}
