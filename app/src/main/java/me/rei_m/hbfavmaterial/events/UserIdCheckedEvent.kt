package me.rei_m.hbfavmaterial.events

public class UserIdCheckedEvent(val type: UserIdCheckedEvent.Companion.Type) {
    companion object {
        public enum class Type {
            OK,
            NG,
            ERROR
        }
    }
}