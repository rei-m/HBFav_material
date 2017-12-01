/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.presentation.widget.fragment

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
