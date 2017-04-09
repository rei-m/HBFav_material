package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.ExplainAppActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.ExplainAppActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.ExplainAppFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter

/**
 * アプリについての情報を表示するActivity.
 */
class ExplainAppActivity : BaseDrawerActivity(), HasComponent<ExplainAppActivityComponent> {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, ExplainAppActivity::class.java)
    }

    private var component: ExplainAppActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onPageSelected(R.id.nav_explain_app)

        if (savedInstanceState == null) {
            setFragment(ExplainAppFragment.newInstance())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_setting -> {
                viewModel.onNavigationSettingSelected()
            }
            R.id.nav_explain_app -> {
            }
            else -> {
                viewModel.onNavigationMainSelected(BookmarkPagerAdapter.Page.forMenuId(item.itemId))
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): ExplainAppActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@ExplainAppActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): ExplainAppActivityComponent {
        val component = (application as App).component
                .plus(ExplainAppActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }
}
