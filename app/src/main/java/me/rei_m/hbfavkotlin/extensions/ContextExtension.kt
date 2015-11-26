package me.rei_m.hbfavkotlin.extensions

import android.content.Context
import android.content.SharedPreferences

fun Context.getAppPreferences(key: String): SharedPreferences {
    return getSharedPreferences(key, Context.MODE_PRIVATE)
}