package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.FragmentInitializeBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.InitializeFragmentComponent
import me.rei_m.hbfavmaterial.presentation.fragment.di.InitializeFragmentModule
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.InitializeFragmentViewModel
import javax.inject.Inject

/**
 * アプリの初期処理を行うFragment.
 */
class InitializeFragment : BaseFragment() {

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
            activity.finish()
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

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .plus(InitializeFragmentModule())
                .inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: InitializeFragmentModule?): InitializeFragmentComponent
    }
}
