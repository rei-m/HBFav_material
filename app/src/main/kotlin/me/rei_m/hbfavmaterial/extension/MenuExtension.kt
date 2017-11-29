package me.rei_m.hbfavmaterial.extension

import android.view.Menu

/**
 * メニューを非表示にする.
 */
fun Menu.hide() {
    for (i_menu in 0 until size()) {
        val item = getItem(i_menu)
        item.isVisible = false
    }
}

/**
 * メニューを表示する.
 */
fun Menu.show() {
    for (i_menu in 0 until size()) {
        val item = getItem(i_menu)
        item.isVisible = true
    }
}
