package me.rei_m.hbfavkotlin.events

import com.squareup.otto.Bus

public final class EventBusHolder {
    companion object {
        public final val EVENT_BUS = Bus()
    }
}
