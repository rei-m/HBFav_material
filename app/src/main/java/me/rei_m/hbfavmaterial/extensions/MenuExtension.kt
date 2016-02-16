package me.rei_m.hbfavmaterial.extensions

import android.view.Menu

/**
 * メニューを非表示にする.
 */
fun Menu.hide() {
    for (i_menu in 0..size() - 1) {
        val item = getItem(i_menu)
        item.isVisible = false
    }
}

/**
 * メニューを表示する.
 */
fun Menu.show() {
    for (i_menu in 0..size() - 1) {
        val item = getItem(i_menu)
        item.isVisible = true
    }
}
