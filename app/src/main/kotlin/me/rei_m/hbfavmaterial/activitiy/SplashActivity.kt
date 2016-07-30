package me.rei_m.hbfavmaterial.activitiy

import android.os.Bundle
import android.support.v7.widget.Toolbar
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.fragments.InitializeFragment

/**
 * 最初に起動するActivity.
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        findViewById(R.id.fab)?.hide()
        if (savedInstanceState == null) {
            setFragment(InitializeFragment.newInstance())
        }
    }
}
