package me.rei_m.hbfavkotlin.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.extensions.setFragment
import me.rei_m.hbfavkotlin.fragments.SplashFragment

public class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            setFragment(SplashFragment.newInstance())
        }
    }
}
