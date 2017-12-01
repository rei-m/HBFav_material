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

package me.rei_m.hbfavmaterial.constant

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import me.rei_m.hbfavmaterial.R

/**
 * 後で見るのフィルタ.
 */
enum class ReadAfterFilter(@IdRes override val menuId: Int,
                           @StringRes override val titleResId: Int) : FilterItem {
    ALL(
            R.id.fragment_bookmark_user_menu_filter_bookmark_all,
            R.string.filter_bookmark_users_all
    ),
    AFTER_READ(
            R.id.fragment_bookmark_user_menu_filter_bookmark_read_after,
            R.string.text_read_after
    );

    companion object {
        fun forMenuId(menuId: Int): ReadAfterFilter {
            values().filter { it.menuId == menuId }.forEach { return it }
            throw AssertionError("no enum found for the id. you forgot to implement?")
        }
    }
}
