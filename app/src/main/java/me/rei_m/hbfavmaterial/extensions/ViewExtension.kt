package me.rei_m.hbfavmaterial.extensions

import android.view.View

/**
 * Viewを非表示にする.
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * Viewを表示する.
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * Viewの表示/非表示を切り替える.
 */
fun View.toggle(isVisible: Boolean) {
    if (isVisible) show() else hide()
}
