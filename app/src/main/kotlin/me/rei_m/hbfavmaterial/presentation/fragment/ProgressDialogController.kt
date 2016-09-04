package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.ProgressDialog
import android.content.Context
import me.rei_m.hbfavmaterial.R

interface ProgressDialogController {

    var progressDialog: ProgressDialog?

    fun showProgressDialog(context: Context) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context).apply {
                setMessage(context.getString(R.string.text_progress_loading))
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
