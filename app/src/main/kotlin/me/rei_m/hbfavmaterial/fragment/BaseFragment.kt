package me.rei_m.hbfavmaterial.fragment

import android.support.v4.app.Fragment
import me.rei_m.hbfavmaterial.activitiy.BaseActivity
import me.rei_m.hbfavmaterial.di.FragmentComponent
import me.rei_m.hbfavmaterial.di.FragmentModule

abstract class BaseFragment : Fragment() {

    val component: FragmentComponent by lazy {
        (activity as BaseActivity).component.plus(FragmentModule(this))
    }
}
