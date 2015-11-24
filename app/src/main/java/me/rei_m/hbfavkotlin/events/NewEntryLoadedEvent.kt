package me.rei_m.hbfavkotlin.events

public class NewEntryLoadedEvent(val type: NewEntryLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}