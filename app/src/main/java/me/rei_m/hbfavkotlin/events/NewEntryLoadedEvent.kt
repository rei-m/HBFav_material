package me.rei_m.hbfavkotlin.events

public data class NewEntryLoadedEvent constructor(val type: NewEntryLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}