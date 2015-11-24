package me.rei_m.hbfavkotlin.events

public class BookmarkOwnLoadedEvent(val type: BookmarkOwnLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}