package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.fragment.ExplainAppFragment
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel
import me.rei_m.hbfavmaterial.viewmodel.activity.di.BaseDrawerActivityViewModelModule

/**
 * アプリについての情報を表示するActivity.
 */
class ExplainAppActivity : BaseDrawerActivity() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, ExplainAppActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onPageSelected(R.id.nav_explain_app)

        if (savedInstanceState == null) {
            setFragment(ExplainAppFragment.newInstance())
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_setting -> {
                onNavigationSettingSelected()
            }
            R.id.nav_explain_app -> {
            }
            else -> {
                onNavigationMainSelected(BookmarkPagerAdapter.Page.forMenuId(item.itemId))
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun provideViewModel(): BaseDrawerActivityViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BaseDrawerActivityViewModel::class.java)

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            BaseDrawerActivityViewModelModule::class,
            ExplainAppFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<ExplainAppActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<ExplainAppActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: ExplainAppActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(ExplainAppActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
