package me.rei_m.hbfavmaterial.events.ui

import me.rei_m.hbfavmaterial.enums.EntryType

class EntryCategoryChangedEvent(val type: EntryType,
                                val target: EntryCategoryChangedEvent.Target) : AbsMainPageFilterChangedEvent() {
    enum class Target {
        HOT,
        NEW
    }
}
