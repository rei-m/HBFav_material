package me.rei_m.hbfavmaterial.constant

import android.content.Context

interface FilterItem {
    val menuId: Int

    val titleResId: Int

    fun title(context: Context): String = context.getString(titleResId)
}
