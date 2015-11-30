package me.rei_m.hbfavmaterial.events

public class NewEntryLoadedEvent(val type: NewEntryLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}