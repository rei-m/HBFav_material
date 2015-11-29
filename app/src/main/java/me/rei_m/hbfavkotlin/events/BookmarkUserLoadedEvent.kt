package me.rei_m.hbfavkotlin.events

public class BookmarkUserLoadedEvent(val type: BookmarkUserLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}