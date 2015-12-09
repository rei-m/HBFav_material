package me.rei_m.hbfavmaterial.events

public class MainPageDisplayEvent(val kind: MainPageDisplayEvent.Companion.Kind) {
    companion object {
        public enum class Kind {
            BOOKMARK_FAVORITE,
            BOOKMARK_OWN,
            HOT_ENTRY,
            NEW_ENTRY,
        }
    }
}