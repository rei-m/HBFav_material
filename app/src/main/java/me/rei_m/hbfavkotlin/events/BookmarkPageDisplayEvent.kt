package me.rei_m.hbfavkotlin.events

public class BookmarkPageDisplayEvent(val kind: BookmarkPageDisplayEvent.Companion.Kind) {
    companion object {
        public enum class Kind {
            BOOKMARK_FAVORITE,
            BOOKMARK_OWN,
            HOT_ENTRY,
            NEW_ENTRY,
        }
    }
}