package me.rei_m.hbfavkotlin.events

public class BookmarkFavoriteLoadedEvent(val type: BookmarkFavoriteLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}