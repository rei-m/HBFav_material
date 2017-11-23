package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.FragmentInitializeBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.InitializeFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.InitializeFragmentViewModelModule
import javax.inject.Inject

/**
 * アプリの初期処理を行うFragment.
 */
class InitializeFragment : DaggerFragment() {

    companion object {
        fun newInstance() = InitializeFragment()
    }

    @Inject
    lateinit var viewModel: InitializeFragmentViewModel

    private var disposable: CompositeDisposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentInitializeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.onCreateView(SnackbarFactory(binding.root))

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.completeSetUpEvent.subscribe {
            activity?.finish()
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
        super.onDestroyView()
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(InitializeFragmentViewModelModule::class))
        internal abstract fun contributeInjector(): InitializeFragment
    }
}
