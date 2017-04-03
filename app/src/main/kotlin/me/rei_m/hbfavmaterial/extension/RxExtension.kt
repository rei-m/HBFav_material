package me.rei_m.hbfavmaterial.extension

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun Completable.subscribeAsync(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit = {}, onEventFinished: () -> Unit = {}): Disposable {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(onEventFinished)
            .subscribe(onSuccess, onFailure)
}

fun <T> Single<T>.subscribeAsync(onSuccess: (T) -> Unit, onFailure: (Throwable) -> Unit = {}, onEventFinished: () -> Unit = {}): Disposable {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(onEventFinished)
            .subscribe(onSuccess, onFailure)
}

fun <T> Observable<T>.subscribeAsync(onSuccess: (T) -> Unit, onFailure: (Throwable) -> Unit = {}, onEventFinished: () -> Unit = {}): Disposable {
    return subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(onEventFinished)
            .subscribe(onSuccess, onFailure)
}

fun <T> Observable<T>.subscribeBus(onSuccess: (T) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSuccess)
}
