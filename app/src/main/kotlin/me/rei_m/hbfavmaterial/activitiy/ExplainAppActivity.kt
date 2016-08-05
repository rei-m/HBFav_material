package me.rei_m.hbfavmaterial.activitiy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.fragment.ExplainAppFragment
import me.rei_m.hbfavmaterial.view.adapter.BookmarkPagerAdaptor

/**
 * アプリについての情報を表示するActivity.
 */
class ExplainAppActivity : BaseDrawerActivity() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, ExplainAppActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

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
            R.id.nav_setting -> {
                navigator.navigateToSetting(this)
                finish()
            }
            R.id.nav_explain_app -> {
            }
            else -> {
                navigator.navigateToMain(this, BookmarkPagerAdaptor.Page.forMenuId(item.itemId))
                finish()
            }
        }

        return super.onNavigationItemSelected(item)
    }
}
