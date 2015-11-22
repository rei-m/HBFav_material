package me.rei_m.hbfavkotlin.events

public data class BookmarkOwnLoadedEvent constructor(val type: BookmarkOwnLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}