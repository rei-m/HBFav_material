package me.rei_m.hbfavmaterial.events.ui

import me.rei_m.hbfavmaterial.utils.BookmarkUtil

class EntryCategoryChangedEvent(val type: BookmarkUtil.Companion.EntryType,
                                       val target: EntryCategoryChangedEvent.Companion.Target) {
    companion object {
        enum class Target {
            HOT,
            NEW
        }
    }
}
