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
 * エントリーの種類.
 */
enum class EntryTypeFilter(@IdRes override val menuId: Int,
                           @StringRes override val titleResId: Int) : FilterItem {
    ALL(
            R.id.fragment_entry_menu_category_all,
            R.string.category_title_all
    ),
    WORLD(
            R.id.fragment_entry_menu_category_world,
            R.string.category_title_world
    ),
    POLITICS_AND_ECONOMY(
            R.id.fragment_entry_menu_category_politics_and_economy,
            R.string.category_title_politics_and_economy
    ),
    LIFE(
            R.id.fragment_entry_menu_category_life,
            R.string.category_title_life
    ),
    ENTERTAINMENT(
            R.id.fragment_entry_menu_category_entertainment,
            R.string.category_title_entertainment
    ),
    STUDY(
            R.id.fragment_entry_menu_category_study,
            R.string.category_title_study
    ),
    TECHNOLOGY(
            R.id.fragment_entry_menu_category_technology,
            R.string.category_title_technology
    ),
    ANIMATION_AND_GAME(
            R.id.fragment_entry_menu_category_animation_and_game,
            R.string.category_title_animation_and_game
    ),
    COMEDY(
            R.id.fragment_entry_menu_category_comedy,
            R.string.category_title_comedy
    );

    companion object {
        fun forMenuId(menuId: Int): EntryTypeFilter {
            values().filter { it.menuId == menuId }.forEach { return it }
            throw AssertionError("no enum found for the id. you forgot to implement?")
        }
    }
}
