package me.rei_m.hbfavmaterial.events

import me.rei_m.hbfavmaterial.utils.BookmarkUtil

public class EntryCategoryChangedEvent(val type: BookmarkUtil.Companion.EntryType,
                                       val target: EntryCategoryChangedEvent.Companion.Target) {
    companion object {
        public enum class Target {
            HOT,
            NEW
        }
    }
}