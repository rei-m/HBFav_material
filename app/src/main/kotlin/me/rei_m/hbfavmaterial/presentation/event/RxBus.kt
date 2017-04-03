package me.rei_m.hbfavmaterial.presentation.event

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RxBus {

    private val bus = PublishSubject.create<Any>()

    fun send(event: Any) {
        bus.onNext(event)
    }

    fun toObservable(): Observable<Any> {
        return bus
    }

    fun hasObservers(): Boolean {
        return bus.hasObservers()
    }
}
