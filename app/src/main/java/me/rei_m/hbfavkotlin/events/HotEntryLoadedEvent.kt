package me.rei_m.hbfavkotlin.events

public data class HotEntryLoadedEvent constructor(val type: HotEntryLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}