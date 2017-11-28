package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.arch.lifecycle.ViewModelProviders
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
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.databinding.FragmentSettingBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.SettingFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.SettingFragmentViewModelModule
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
    lateinit var hatenaService: HatenaService

    @Inject
    lateinit var twitterService: TwitterService

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var viewModelFactory: SettingFragmentViewModel.Factory

    private lateinit var binding: FragmentSettingBinding

    private lateinit var viewModel: SettingFragmentViewModel

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.onClickHatenaAuthStatus.subscribe {
            navigator.navigateToOAuth()
        }, viewModel.showEditHatenaIdDialogEvent.subscribe {
            listener?.onShowEditHatenaIdDialog()
        }, hatenaService.confirmAuthorisedEvent.subscribe {
            viewModel.isAuthorisedHatena.set(it)
        }, twitterService.confirmAuthorisedEvent.subscribe {
            viewModel.isAuthorisedTwitter.set(it)
        }, viewModel.startAuthoriseTwitterEvent.subscribe {
            listener?.onStartAuthoriseTwitter()
        })

        hatenaService.confirmAuthorised()
        twitterService.confirmAuthorised()
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Navigator.REQ_CODE_OAUTH -> {

                data ?: return

                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val isDone = data.extras.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)
                    val isAuthorise = data.extras.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS)
                    if (isDone) {
                        viewModel.isAuthorisedHatena.set(isAuthorise)
                    } else {
                        // 認可を選択せずにresultCodeが設定された場合はネットワークエラーのケース.
                        SnackbarFactory(binding.root).create(R.string.message_error_network).show()
                    }
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
