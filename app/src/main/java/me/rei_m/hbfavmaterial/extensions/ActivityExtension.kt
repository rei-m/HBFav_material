package me.rei_m.hbfavmaterial.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import me.rei_m.hbfavmaterial.R

fun AppCompatActivity.startActivityWithClearTop(intent: Intent) {
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
    finish()
}

fun AppCompatActivity.setFragment(fragment: Fragment,
                                  containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment)
            .commit();
}

fun AppCompatActivity.replaceFragment(fragment: Fragment,
                                      containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit();
}

fun AppCompatActivity.setFragment(fragment: Fragment,
                                  tag: String,
                                  containerId: Int = R.id.content) {
    supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment, tag)
            .commit();
}

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

fun AppCompatActivity.hideKeyBoard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun AppCompatActivity.showSnackbarNetworkError(view: View) {
    Snackbar.make(view, getString(R.string.message_error_network), Snackbar.LENGTH_LONG).setAction("Action", null).show()
}

fun AppCompatActivity.openUrl(url: String) {
    val intent = android.content.Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}