package me.rei_m.hbfavmaterial.events

import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer

final class EventBusHolder {
    companion object {
        final val EVENT_BUS = Bus(ThreadEnforcer.ANY)
    }
}
