package me.rei_m.hbfavkotlin.fragments

import android.app.ProgressDialog
import android.content.Context

interface ProgressDialogI {

    var mProgressDialog: ProgressDialog?

    fun showProgressDialog(context: Context) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(context)
            mProgressDialog!!.setMessage("Now Loading...")
            mProgressDialog!!.setCanceledOnTouchOutside(false)
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