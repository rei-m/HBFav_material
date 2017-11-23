package me.rei_m.hbfavmaterial.presentation.activity

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import dagger.Binds
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.multibindings.IntoMap
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivitySplashBinding
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment

/**
 * 最初に起動するActivity.
 */
class SplashActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            setFragment(InitializeFragment.newInstance(), InitializeFragment::class.java.simpleName)
        }
    }

    @ForActivity
    @dagger.Subcomponent(modules = arrayOf(
            ActivityModule::class,
            InitializeFragment.Module::class)
    )
    interface Subcomponent : AndroidInjector<SplashActivity> {
        @dagger.Subcomponent.Builder
        abstract class Builder : AndroidInjector.Builder<SplashActivity>() {

            abstract fun activityModule(module: ActivityModule): Builder

            override fun seedInstance(instance: SplashActivity) {
                activityModule(ActivityModule(instance))
            }
        }
    }

    @dagger.Module(subcomponents = arrayOf(Subcomponent::class))
    abstract inner class Module {
        @Binds
        @IntoMap
        @ActivityKey(SplashActivity::class)
        internal abstract fun bind(builder: Subcomponent.Builder): AndroidInjector.Factory<out Activity>
    }
}
