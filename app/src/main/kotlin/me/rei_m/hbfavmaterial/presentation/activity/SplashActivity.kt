package me.rei_m.hbfavmaterial.presentation.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ActivitySplashBinding
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.presentation.activity.di.ActivityModule
import me.rei_m.hbfavmaterial.presentation.activity.di.SplashActivityComponent
import me.rei_m.hbfavmaterial.presentation.activity.di.SplashActivityModule
import me.rei_m.hbfavmaterial.presentation.fragment.InitializeFragment

/**
 * 最初に起動するActivity.
 */
class SplashActivity : BaseActivity(),
        HasComponent<SplashActivityComponent> {

    private var component: SplashActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivitySplashBinding>(this, R.layout.activity_splash)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            setFragment(InitializeFragment.newInstance(), InitializeFragment::class.java.simpleName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    override fun setUpActivityComponent() {
        component = createActivityComponent()
    }

    override fun getComponent(): SplashActivityComponent = component ?: let {
        val component = createActivityComponent()
        this@SplashActivity.component = component
        return@let component
    }

    private fun createActivityComponent(): SplashActivityComponent {
        val component = (application as App).component
                .plus(SplashActivityModule(), ActivityModule(this))
        component.inject(this)
        return component
    }
}
