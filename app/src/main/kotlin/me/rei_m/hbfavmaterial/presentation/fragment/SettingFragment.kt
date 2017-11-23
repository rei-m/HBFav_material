package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.databinding.FragmentSettingBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.fragment.SettingFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.di.SettingFragmentViewModelModule
import javax.inject.Inject

/**
 * ユーザーの設定を行うFragment.
 */
class SettingFragment : DaggerFragment() {

    companion object {

        val TAG: String = SettingFragment::class.java.simpleName

        fun newInstance() = SettingFragment()
    }

    @Inject
    lateinit var viewModel: SettingFragmentViewModel

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.onCreateView(SnackbarFactory(binding.root))

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.showEditHatenaIdDialogEvent.subscribe {
            listener?.onShowEditHatenaIdDialog()
        }, viewModel.startAuthoriseTwitterEvent.subscribe {
            listener?.onStartAuthoriseTwitter()
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
        disposable?.dispose()
        disposable = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    override fun onDestroyView() {
        viewModel.onDestroyView()
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Navigator.REQ_CODE_OAUTH -> {

                data ?: return

                if (resultCode == AppCompatActivity.RESULT_OK) {
                    viewModel.onAuthoriseHatena(data.extras.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE),
                            data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS))
                }
                return
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onShowEditHatenaIdDialog()

        fun onStartAuthoriseTwitter()
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(SettingFragmentViewModelModule::class))
        internal abstract fun contributeInjector(): SettingFragment
    }
}
