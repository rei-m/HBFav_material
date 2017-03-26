package me.rei_m.hbfavmaterial.constant

import me.rei_m.hbfavmaterial.R

/**
 * エントリーの種類.
 */
enum class EntryTypeFilter(override val menuId: Int,
                           override val titleResId: Int) : FilterItem {
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
            for (value: EntryTypeFilter in values()) {
                if (value.menuId == menuId) {
                    return value
                }
            }
            throw AssertionError("no enum found for the id. you forgot to implement?")
        }
    }
}
