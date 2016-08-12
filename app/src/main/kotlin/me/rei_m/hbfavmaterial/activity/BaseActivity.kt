package me.rei_m.hbfavmaterial.activity

import android.support.v7.app.AppCompatActivity
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.di.ActivityComponent

abstract class BaseActivity : AppCompatActivity() {

    val component: ActivityComponent by lazy {
        (applicationContext as App).component.activityComponent()
    }
}
