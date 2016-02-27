package me.rei_m.hbfavmaterial.enums

import me.rei_m.hbfavmaterial.R

/**
 * 後で見るのフィルタ.
 */
enum class ReadAfterType(override val menuId: Int,
                         override val titleResId: Int) : FilterItemI {
    ALL(
            R.id.menu_filter_bookmark_all,
            R.string.filter_bookmark_users_all

    ),
    AFTER_READ(
            R.id.menu_filter_bookmark_read_after,
            R.string.text_read_after
    );
}
