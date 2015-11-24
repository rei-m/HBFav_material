package me.rei_m.hbfavkotlin.events

public class HotEntryLoadedEvent(val type: HotEntryLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}