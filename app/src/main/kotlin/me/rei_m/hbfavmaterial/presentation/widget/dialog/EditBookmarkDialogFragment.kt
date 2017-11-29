package me.rei_m.hbfavmaterial.presentation.widget.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import dagger.Binds
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.DialogFragmentEditBookmarkBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.extension.adjustScreenWidth
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.EditBookmarkDialogFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.di.EditBookmarkDialogFragmentViewModelModule
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
    lateinit var navigator: Navigator

    @Inject
    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var viewModelFactory: EditBookmarkDialogFragmentViewModel.Factory

    private var binding: DialogFragmentEditBookmarkBinding? = null

    private lateinit var viewModel: EditBookmarkDialogFragmentViewModel

    private var disposable: CompositeDisposable? = null

    private val articleTitle: String by lazy {
        requireNotNull(arguments?.getString(ARG_ARTICLE_TITLE)) {
            "Arguments is NULL $ARG_ARTICLE_TITLE"
        }
    }

    private val articleUrl: String by lazy {
        requireNotNull(arguments?.getString(ARG_ARTICLE_URL)) {
            "Arguments is NULL $ARG_ARTICLE_URL"
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(EditBookmarkDialogFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DialogFragmentEditBookmarkBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.isLoading.subscribe {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }, viewModel.dismissDialogEvent.subscribe {
            dismiss()
        }, viewModel.hatenaUnauthorizedEvent.subscribe {
            navigator.navigateToOAuth()
            dismiss()
        }, viewModel.twitterUnauthorizedEvent.subscribe {
            navigator.navigateToSetting()
            dismiss()
        }, viewModel.raisedErrorEvent.subscribe {
            binding?.root?.let {
                SnackbarFactory(it).create(R.string.message_error_network).show()
            }
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

    @ForFragment
    @dagger.Subcomponent(modules = arrayOf(EditBookmarkDialogFragmentViewModelModule::class))
    interface Subcomponent : AndroidInjector<EditBookmarkDialogFragment> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<EditBookmarkDialogFragment>() {

            abstract fun viewModelModule(module: EditBookmarkDialogFragmentViewModelModule): Builder

            override fun seedInstance(instance: EditBookmarkDialogFragment) {
                viewModelModule(EditBookmarkDialogFragmentViewModelModule(instance.articleTitle, instance.articleUrl))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @FragmentKey(EditBookmarkDialogFragment::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Fragment>
    }
}
