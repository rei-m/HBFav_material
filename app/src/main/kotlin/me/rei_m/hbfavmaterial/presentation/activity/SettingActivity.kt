package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import com.twitter.sdk.android.core.TwitterAuthConfig
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ActivityModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.SettingActivityComponent
import me.rei_m.hbfavmaterial.di.SettingActivityModule
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.presentation.fragment.SettingFragment
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdaptor

class SettingActivity : BaseDrawerActivity(),
        HasComponent<SettingActivityComponent>,
        SettingFragment.OnFragmentInteractionListener {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }

    private lateinit var component: SettingActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById(R.id.pager)?.hide()
        findViewById(R.id.content)?.show()

        with(findViewById(R.id.activity_main_nav) as NavigationView) {
            setCheckedItem(R.id.nav_setting)
        }

        if (savedInstanceState == null) {
            setFragment(SettingFragment.newInstance(), SettingFragment.TAG)
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_setting -> {
            }
            R.id.nav_explain_app -> {
                navigator.navigateToExplainApp()
                finish()
            }
            else -> {
                navigator.navigateToMain(BookmarkPagerAdaptor.Page.forMenuId(item.itemId))
                finish()
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ActivityNavigator.REQ_CODE_OAUTH,
            TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE -> {
                val fragment = supportFragmentManager.findFragmentByTag(SettingFragment.TAG)
                fragment?.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onUserIdUpdated(userId: String) {
        displayUserIconAndName(userId)
    }

    override fun setupActivityComponent() {
        component = (application as App).component
                .plus(SettingActivityModule(), ActivityModule(this))
        component.inject(this)
    }

    override fun getComponent(): SettingActivityComponent {
        return component
    }
}
