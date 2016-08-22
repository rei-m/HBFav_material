package me.rei_m.hbfavmaterial.testutil

import android.os.Bundle
import android.support.v7.widget.Toolbar
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activity.BaseActivity
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.setFragment
import me.rei_m.hbfavmaterial.fragment.InitializeFragment

class DriverActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity)
        val toolbar = findViewById(R.id.activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        findViewById(R.id.fab)?.hide()
        if (savedInstanceState == null) {
            setFragment(InitializeFragment.newInstance(), InitializeFragment::class.java.simpleName)
        }
    }
}
