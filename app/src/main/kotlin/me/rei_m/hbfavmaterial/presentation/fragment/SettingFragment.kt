package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.databinding.FragmentSettingBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.SettingFragmentComponent
import me.rei_m.hbfavmaterial.di.SettingFragmentModule
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.SettingFragmentViewModel
import javax.inject.Inject

/**
 * ユーザーの設定を行うFragment.
 */
class SettingFragment : BaseFragment() {

    companion object {

        val TAG: String = SettingFragment::class.java.simpleName

        fun newInstance() = SettingFragment()
    }

    @Inject
    lateinit var viewModel: SettingFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentSettingBinding.inflate(inflater, container, false)
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

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .plus(SettingFragmentModule(this))
                .inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: SettingFragmentModule?): SettingFragmentComponent
    }
}
