package me.rei_m.hbfavmaterial.presentation.activity

import android.app.ProgressDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivitySplashBinding
import me.rei_m.hbfavmaterial.di.ActivityModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.SplashActivityComponent
import me.rei_m.hbfavmaterial.di.SplashActivityModule
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment
import me.rei_m.hbfavmaterial.presentation.fragment.ProgressDialogController
import javax.inject.Inject

/**
 * 最初に起動するActivity.
 */
class SplashActivity : AppCompatActivity(),
        HasComponent<SplashActivityComponent>,
        ProgressDialogController {

    override var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var rxBus: RxBus

    private var component: SplashActivityComponent? = null

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = createActivityComponent()

        val binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            setFragment(InitializeFragment.newInstance(), InitializeFragment::class.java.simpleName)
        }
    }

    override fun getComponent(): SplashActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@SplashActivity.component = component
        return@let component
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.add(rxBus.toObservable().subscribeBus({
            when (it) {
                is FinishActivityEvent -> {
                    finish()
                }
                is ShowProgressDialogEvent -> {
                    showProgressDialog(this)
                }
                is DismissProgressDialogEvent -> {
                    closeProgressDialog()
                }
                is FailToConnectionEvent -> {
                    showFailToConnectionMessage()
                }
            }
        }))
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable = null
    }

    private fun createActivityComponent(): SplashActivityComponent {
        val component = (application as App).component
                .plus(SplashActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }

    private fun showFailToConnectionMessage() {
        Snackbar.make(findViewById(R.id.content), getString(R.string.message_error_network), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}
