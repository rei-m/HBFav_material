package me.rei_m.hbfavmaterial.fragment

import android.app.ProgressDialog
import android.content.Context

interface ProgressDialogController {

    var progressDialog: ProgressDialog?

    fun showProgressDialog(context: Context) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context).apply {
                setMessage("Now Loading...")
                setCanceledOnTouchOutside(false)
            }
        }
        
        progressDialog?.run {
            if (!isShowing) {
                show()
            }
        }
    }

    fun closeProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}
