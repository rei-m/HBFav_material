package me.rei_m.hbfavmaterial.extensions

import android.support.v4.app.DialogFragment

fun DialogFragment.adjustScreenWidth() {
    dialog.window.attributes.apply {
        val dialogWidth = resources.displayMetrics.widthPixels * 0.9
        width = dialogWidth.toInt()
    }
}
