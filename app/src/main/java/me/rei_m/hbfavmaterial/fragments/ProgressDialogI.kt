package me.rei_m.hbfavmaterial.fragments

import android.app.ProgressDialog
import android.content.Context

interface ProgressDialogI {

    var mProgressDialog: ProgressDialog?

    fun showProgressDialog(context: Context) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(context).apply {
                setMessage("Now Loading...")
                setCanceledOnTouchOutside(false)
            }
        }
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
    }

    fun closeProgressDialog() {
        mProgressDialog?.dismiss()
        mProgressDialog = null
    }
}
