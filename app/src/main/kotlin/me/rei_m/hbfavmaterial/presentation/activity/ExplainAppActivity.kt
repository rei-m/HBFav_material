package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ExplainAppActivityComponent
import me.rei_m.hbfavmaterial.di.ExplainAppActivityModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.presentation.fragment.ExplainAppFragment
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdaptor

/**
 * アプリについての情報を表示するActivity.
 */
class ExplainAppActivity : BaseDrawerActivity(), HasComponent<ExplainAppActivityComponent> {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, ExplainAppActivity::class.java)
    }

    private lateinit var component: ExplainAppActivityComponent

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

    override fun setupActivityComponent() {
        component = (application as App).component
                .plus(ExplainAppActivityModule(this))
        component.inject(this)
    }

    override fun getComponent(): ExplainAppActivityComponent {
        return component
    }
}
