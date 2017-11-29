package me.rei_m.hbfavmaterial.presentation.helper

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

class SnackbarFactory(private val view: View) {
    fun create(@StringRes messageId: Int): Snackbar {
        return Snackbar.make(view, view.context.getString(messageId), Snackbar.LENGTH_SHORT).setAction("Action", null)
    }
}
