package me.rei_m.hbfavmaterial.events

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