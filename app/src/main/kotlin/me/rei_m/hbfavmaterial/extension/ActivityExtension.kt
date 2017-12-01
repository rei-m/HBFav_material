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

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import me.rei_m.hbfavmaterial.R

/**
 * Fragmentをセットする.
 */
fun AppCompatActivity.setFragment(fragment: Fragment,
                                  containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment)
            .commit();
}

/**
 * Fragmentを置き換える.
 */
fun AppCompatActivity.replaceFragment(fragment: Fragment,
                                      containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit();
}

/**
 * Fragmentをセットする(Tag付き).
 */
fun AppCompatActivity.setFragment(fragment: Fragment,
                                  tag: String,
                                  containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment, tag)
            .commit();
}

/**
 * Fragmentを置き換える(Tag付き).
 */
fun AppCompatActivity.replaceFragment(fragment: Fragment,
                                      tag: String,
                                      containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(containerId, fragment, tag)
            .addToBackStack(null)
            .commit();
}

/**
 * キーボードを隠す.
 */
fun AppCompatActivity.hideKeyBoard(view: View?) {

    view ?: return

    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

/**
 * ネットワークエラー通知ののSnackbarを表示する
 */
fun AppCompatActivity.showSnackbarNetworkError(view: View = findViewById(android.R.id.content)) {
    Snackbar.make(view, getString(R.string.message_error_network), Snackbar.LENGTH_LONG).setAction("Action", null).show()
}
