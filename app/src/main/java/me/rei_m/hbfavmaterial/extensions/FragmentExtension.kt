package me.rei_m.hbfavmaterial.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment

fun Fragment.getAppContext(): Context {
    return activity.applicationContext
}

fun Fragment.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}