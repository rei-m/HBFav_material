package me.rei_m.hbfavmaterial.events

public class BookmarkFavoriteLoadedEvent(val type: BookmarkFavoriteLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}