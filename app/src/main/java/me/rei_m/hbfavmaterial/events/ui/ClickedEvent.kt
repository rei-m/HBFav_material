package me.rei_m.hbfavmaterial.events.ui

public class ClickedEvent(val type: ClickedEvent.Companion.Type) {
    companion object {
        enum class Type {
            FROM_DEVELOPER,
            CREDIT
        }
    }
}
