package me.rei_m.hbfavmaterial.presentation.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import com.twitter.sdk.android.core.TwitterAuthConfig
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.SettingActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.SettingActivityModule
import me.rei_m.hbfavmaterial.presentation.event.*
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogFragment
import me.rei_m.hbfavmaterial.presentation.fragment.ProgressDialogController
import me.rei_m.hbfavmaterial.presentation.fragment.SettingFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import javax.inject.Inject

class SettingActivity : BaseDrawerActivity(),
        ProgressDialogController,
        HasComponent<SettingActivityComponent> {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }

    override var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var rxBus: RxBus

    @Inject
    lateinit var twitterService: TwitterService

    private var component: SettingActivityComponent? = null

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onPageSelected(R.id.nav_setting)

        if (savedInstanceState == null) {
            setFragment(SettingFragment.newInstance(), SettingFragment.TAG)
        }
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.add(rxBus.toObservable().subscribeBus({
            when (it) {
                is ShowEditHatenaIdDialogEvent -> {
                    if (supportFragmentManager.findFragmentByTag(EditUserIdDialogFragment.TAG) == null) {
                        EditUserIdDialogFragment.newInstance().show(supportFragmentManager, EditUserIdDialogFragment.TAG)
                    }
                }
                is StartAuthoriseTwitterEvent -> {
                    twitterService.authorize(this)
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
                is FinishActivityEvent -> {
                    finish()
                }
            }
        }))
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_setting -> {
            }
            R.id.nav_explain_app -> {
                viewModel.onNavigationExplainAppSelected()
            }
            else -> {
                viewModel.onNavigationMainSelected(BookmarkPagerAdapter.Page.forMenuId(item.itemId))
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE -> {
                // TwitterOAuth認可後の処理を行う.
                twitterService.onActivityResult(requestCode, resultCode, data)
                return
            }
            else -> {
                val fragment = supportFragmentManager.findFragmentByTag(SettingFragment.TAG)
                fragment?.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): SettingActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@SettingActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): SettingActivityComponent {
        val component = (application as App).component
                .plus(SettingActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }

    private fun showFailToConnectionMessage() {
        Snackbar.make(findViewById(R.id.content), getString(R.string.message_error_network), Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}
