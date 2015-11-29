package me.rei_m.hbfavkotlin.events

public class UserRegisterBookmarkLoadedEvent(val type: UserRegisterBookmarkLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}