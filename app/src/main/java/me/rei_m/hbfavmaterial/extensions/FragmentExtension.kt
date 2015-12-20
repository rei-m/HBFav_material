package me.rei_m.hbfavmaterial.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment

/**
 * Application Contextを取得する.
 */
fun Fragment.getAppContext(): Context {
    return activity.applicationContext
}

/**
 * 外部URLを開くIntentを起動する.
 */
fun Fragment.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}
