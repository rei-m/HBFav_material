package me.rei_m.hbfavkotlin.extensions

import android.content.Context
import android.support.v4.app.Fragment

fun Fragment.getAppContext(): Context {
    return activity.applicationContext
}
