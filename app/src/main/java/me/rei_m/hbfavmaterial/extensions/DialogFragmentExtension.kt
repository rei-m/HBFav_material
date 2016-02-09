package me.rei_m.hbfavmaterial.extensions

import android.support.v4.app.DialogFragment

/**
 * DialogFragmentの幅を画面に合うように調整する.
 */
fun DialogFragment.adjustScreenWidth() {
    with(dialog.window.attributes) {
        val dialogWidth = resources.displayMetrics.widthPixels * 0.9
        width = dialogWidth.toInt()
    }
}
