/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
