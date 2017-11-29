package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.twitter.sdk.android.core.TwitterAuthConfig
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter
import me.rei_m.hbfavmaterial.presentation.widget.dialog.EditUserIdDialogFragment
import me.rei_m.hbfavmaterial.presentation.widget.fragment.SettingFragment
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel
import me.rei_m.hbfavmaterial.viewmodel.activity.di.BaseDrawerActivityViewModelModule
import javax.inject.Inject

class SettingActivity : BaseDrawerActivity(),
        SettingFragment.OnFragmentInteractionListener {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }

    @Inject
    lateinit var twitterService: TwitterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onPageSelected(R.id.nav_setting)

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
                onNavigationExplainAppSelected()
            }
            else -> {
                onNavigationMainSelected(BookmarkPagerAdapter.Page.forMenuId(item.itemId))
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

    override fun onShowEditHatenaIdDialog() {
        if (supportFragmentManager.findFragmentByTag(EditUserIdDialogFragment.TAG) == null) {
            EditUserIdDialogFragment.newInstance().show(supportFragmentManager, EditUserIdDialogFragment.TAG)
        }
    }

    override fun onStartAuthoriseTwitter() {
        twitterService.authorize(this)
    }

    override fun provideViewModel(): BaseDrawerActivityViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(BaseDrawerActivityViewModel::class.java)

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            BaseDrawerActivityViewModelModule::class,
            SettingFragment.Module::class,
            EditUserIdDialogFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<SettingActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<SettingActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: SettingActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(SettingActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
