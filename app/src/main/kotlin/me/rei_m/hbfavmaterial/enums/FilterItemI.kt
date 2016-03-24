package me.rei_m.hbfavmaterial.enums

import android.content.Context

interface FilterItemI {
    companion object {
        fun forMenuId(menuId: Int): FilterItemI {
            for (filter: FilterItemI in ReadAfterFilter.values()) {
                if (filter.menuId == menuId) {
                    return filter
                }
            }
            for (filter: FilterItemI in EntryTypeFilter.values()) {
                if (filter.menuId == menuId) {
                    return filter
                }
            }
            for (filter: FilterItemI in BookmarkCommentFilter.values()) {
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
