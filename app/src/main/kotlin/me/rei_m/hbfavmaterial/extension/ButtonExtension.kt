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
