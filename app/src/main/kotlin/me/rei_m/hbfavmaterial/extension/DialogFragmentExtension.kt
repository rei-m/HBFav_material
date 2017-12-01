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

package me.rei_m.hbfavmaterial.extension

import android.support.v4.app.DialogFragment

/**
 * DialogFragmentの幅を画面に合うように調整する.
 */
fun DialogFragment.adjustScreenWidth() {
    with(dialog.window.attributes) {
        val dialogWidth = resources.displayMetrics.widthPixels * 0.9
        width = dialogWidth.toInt()
    }
}
