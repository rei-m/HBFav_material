package me.rei_m.hbfavmaterial.events.ui

import me.rei_m.hbfavmaterial.enums.EntryTypeFilter

class EntryCategoryChangedEvent(val typeFilter: EntryTypeFilter,
                                val target: EntryCategoryChangedEvent.Target) {
    enum class Target {
        HOT,
        NEW
    }
}
