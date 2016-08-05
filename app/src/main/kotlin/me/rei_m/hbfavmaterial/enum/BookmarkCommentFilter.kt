package me.rei_m.hbfavmaterial.enum

import me.rei_m.hbfavmaterial.R

/**
 * ブックマークのフィルタ.
 */
enum class BookmarkCommentFilter(override val menuId: Int,
                                 override val titleResId: Int) : FilterItem {
    ALL(
            R.id.fragment_bookmarked_users_menu_filter_users_all,
            R.string.filter_bookmark_users_all
    ),
    COMMENT(
            R.id.fragment_bookmarked_users_menu_menu_filter_users_comment,
            R.string.filter_bookmark_users_comment
    );
}
