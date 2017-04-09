package me.rei_m.hbfavmaterial.presentation.helper

import android.support.annotation.IdRes
import android.support.design.widget.Snackbar
import android.view.View

class SnackbarFactory(private val view: View) {
    fun create(@IdRes messageId: Int): Snackbar {
        return Snackbar.make(view, view.context.getString(messageId), Snackbar.LENGTH_SHORT).setAction("Action", null)
    }
}
