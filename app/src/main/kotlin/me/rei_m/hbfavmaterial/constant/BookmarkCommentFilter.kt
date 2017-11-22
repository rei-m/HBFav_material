package me.rei_m.hbfavmaterial.constant

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import me.rei_m.hbfavmaterial.R

/**
 * ブックマークのフィルタ.
 */
enum class BookmarkCommentFilter(@IdRes override val menuId: Int,
                                 @StringRes override val titleResId: Int) : FilterItem {
    ALL(
            R.id.fragment_bookmarked_users_menu_filter_users_all,
            R.string.filter_bookmark_users_all
    ),
    COMMENT(
            R.id.fragment_bookmarked_users_menu_menu_filter_users_comment,
            R.string.filter_bookmark_users_comment
    );

    companion object {
        fun forMenuId(menuId: Int): BookmarkCommentFilter {
            values().filter { it.menuId == menuId }.forEach { return it }
            throw AssertionError("no enum found for the id. you forgot to implement?")
        }
    }

}
