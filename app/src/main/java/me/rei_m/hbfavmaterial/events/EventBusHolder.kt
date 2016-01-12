package me.rei_m.hbfavmaterial.events

import com.squareup.otto.Bus

final class EventBusHolder {
    companion object {
        final val EVENT_BUS = Bus()
    }
}
