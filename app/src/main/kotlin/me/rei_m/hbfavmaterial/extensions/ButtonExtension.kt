package me.rei_m.hbfavmaterial.extensions

import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import me.rei_m.hbfavmaterial.R

/**
 * Buttonを有効にする.
 */
fun AppCompatButton.enable() {
    isEnabled = true
    setTextColor(ContextCompat.getColor(context, R.color.app_accent_color))
}

/**
 * Buttonを無効にする.
 */
fun AppCompatButton.disable() {
    isEnabled = false
    setTextColor(ContextCompat.getColor(context, R.color.button_disable))
}

/**
 * Buttonの有効/無効を切り替える.
 */
fun AppCompatButton.toggle(isEnabled: Boolean) {
    if (isEnabled) enable() else disable()
}
