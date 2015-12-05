package me.rei_m.hbfavmaterial.extensions

import android.support.v7.widget.AppCompatButton
import me.rei_m.hbfavmaterial.R

fun AppCompatButton.enable() {
    isEnabled = true
    setTextColor(resources.getColor(R.color.app_accent_color, null))
}

fun AppCompatButton.disable() {
    isEnabled = false
    setTextColor(resources.getColor(R.color.button_disable, null))
}

fun AppCompatButton.toggle(isEnabled: Boolean) {
    if (isEnabled) {
        enable()
    } else {
        disable()
    }
}