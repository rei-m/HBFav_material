package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import com.squareup.otto.Subscribe
import com.twitter.sdk.android.core.TwitterAuthConfig
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.ui.UserIdChangedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.fragments.SettingFragment
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor

class SettingActivity : BaseActivityWithDrawer() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }

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
                startActivityWithClearTop(ExplainAppActivity.createIntent(this))
            }
            else ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.Page.forMenuId(item.itemId).index))
        }

        return super.onNavigationItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            val fragment = supportFragmentManager.findFragmentByTag(SettingFragment.TAG)
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Subscribe
    fun subscribe(event: UserIdChangedEvent) {
        (applicationContext as App).resetBookmarks()
        displayUserIconAndName(event.newId)
    }
}
