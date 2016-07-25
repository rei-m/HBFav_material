package me.rei_m.hbfavmaterial.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.fragments.ExplainAppFragment
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor

/**
 * アプリについての情報を表示するActivity.
 */
class ExplainAppActivity : BaseDrawerActivity() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, ExplainAppActivity::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById(R.id.pager)?.hide()
        findViewById(R.id.content)?.show()

        val navigationView = findViewById(R.id.activity_main_nav) as NavigationView
        navigationView.setCheckedItem(R.id.nav_explain_app)

        if (savedInstanceState == null) {
            setFragment(ExplainAppFragment.newInstance())
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_setting ->
                startActivityWithClearTop(SettingActivity.createIntent(this))
            R.id.nav_explain_app -> {
            }
            else ->
                startActivityWithClearTop(MainActivity.createIntent(this, BookmarkPagerAdaptor.Page.forMenuId(item.itemId).index))
        }

        return super.onNavigationItemSelected(item)
    }
}
