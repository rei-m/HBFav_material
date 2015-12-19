package me.rei_m.hbfavmaterial.extensions

import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.toggle(isVisible: Boolean) {
    if (isVisible) {
        show()
    } else {
        hide()
    }
}