package me.rei_m.hbfavmaterial.extensions

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import me.rei_m.hbfavmaterial.R

fun AppCompatActivity.setFragment(fragment: Fragment,
                                  containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment)
            .commit();
}

fun AppCompatActivity.replaceFragment(fragment: Fragment,
                                      containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit();
}
