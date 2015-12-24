package me.rei_m.hbfavmaterial.fragments

import android.app.ProgressDialog
import android.content.Context

interface ProgressDialogI {

    var mProgressDialog: ProgressDialog?

    fun showProgressDialog(context: Context) {
        mProgressDialog ?: ProgressDialog(context).apply {
            setMessage("Now Loading...")
            setCanceledOnTouchOutside(false)
        }

        mProgressDialog?.apply {
            if (!isShowing) {
                show()
            }
        }
    }

    fun closeProgressDialog() {
        mProgressDialog?.dismiss()
        mProgressDialog = null
    }
}
