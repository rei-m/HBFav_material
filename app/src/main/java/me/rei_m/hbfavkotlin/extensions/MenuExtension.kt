package me.rei_m.hbfavkotlin.extensions

import android.view.Menu

fun Menu.hide() {
    for (i_menu in 0..size() - 1) {
        val item = getItem(i_menu)
        item.setVisible(false)
    }
}

fun Menu.show() {
    for (i_menu in 0..size() - 1) {
        val item = getItem(i_menu)
        item.setVisible(true)
    }
}