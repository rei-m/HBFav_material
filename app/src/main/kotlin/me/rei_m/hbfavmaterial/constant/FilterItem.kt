package me.rei_m.hbfavmaterial.constant

import android.content.Context

interface FilterItem {
    companion object {
        fun forMenuId(menuId: Int): FilterItem {
            for (filter: FilterItem in ReadAfterFilter.values()) {
                if (filter.menuId == menuId) {
                    return filter
                }
            }
            for (filter: FilterItem in EntryTypeFilter.values()) {
                if (filter.menuId == menuId) {
                    return filter
                }
            }
            for (filter: FilterItem in BookmarkCommentFilter.values()) {
                if (filter.menuId == menuId) {
                    return filter
                }
            }
            throw AssertionError("no enum found for the id. you forgot to implement?")
        }
    }

    val menuId: Int

    val titleResId: Int

    fun title(context: Context): String = context.getString(titleResId)
}
