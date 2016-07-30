package me.rei_m.hbfavmaterial.activitiy

import android.support.v7.app.AppCompatActivity
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.di.ActivityComponent
import me.rei_m.hbfavmaterial.di.ActivityModule

abstract class BaseActivity : AppCompatActivity() {

    val component: ActivityComponent by lazy {
        (applicationContext as App).component.plus(ActivityModule(this))
    }

}
