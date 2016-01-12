package me.rei_m.hbfavmaterial.events.ui

class MainPageDisplayEvent(val kind: MainPageDisplayEvent.Companion.Kind) {
    companion object {
        enum class Kind {
            BOOKMARK_FAVORITE,
            BOOKMARK_OWN,
            HOT_ENTRY,
            NEW_ENTRY,
        }
    }
}
