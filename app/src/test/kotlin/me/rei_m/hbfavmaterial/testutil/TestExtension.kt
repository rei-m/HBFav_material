package me.rei_m.hbfavmaterial.testutil

import android.support.annotation.IdRes
import android.view.View

@Suppress("UNCHECKED_CAST")
fun <T : View> View.bindView(@IdRes id: Int): Lazy<T> = lazy {
    findViewById(id) as T
}
