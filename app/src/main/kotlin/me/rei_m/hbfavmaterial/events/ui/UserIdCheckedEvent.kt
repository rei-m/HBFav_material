package me.rei_m.hbfavmaterial.events.ui

class UserIdCheckedEvent(val type: UserIdCheckedEvent.Companion.Type) {
    companion object {
        enum class Type {
            OK,
            NG,
            ERROR
        }
    }
}
