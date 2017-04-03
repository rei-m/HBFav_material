package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.databinding.FragmentInitializeBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.InitializeFragmentComponent
import me.rei_m.hbfavmaterial.di.InitializeFragmentModule
import me.rei_m.hbfavmaterial.presentation.viewmodel.InitializeFragmentViewModel
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentInitializeBinding.inflate(inflater, container, false)
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
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .plus(InitializeFragmentModule(this))
                .inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: InitializeFragmentModule?): InitializeFragmentComponent
    }
}
