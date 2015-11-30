package me.rei_m.hbfavmaterial.events

public class UserRegisterBookmarkLoadedEvent(val type: UserRegisterBookmarkLoadedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            COMPLETE,
            ERROR
        }
    }
}