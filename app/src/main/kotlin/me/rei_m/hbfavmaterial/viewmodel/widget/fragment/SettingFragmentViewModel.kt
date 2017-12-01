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

package me.rei_m.hbfavmaterial.viewmodel.widget.fragment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.model.UserModel

class SettingFragmentViewModel(userModel: UserModel) : ViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val isAuthorisedHatena: ObservableBoolean = ObservableBoolean(false)

    val isAuthorisedTwitter: ObservableBoolean = ObservableBoolean(false)

    private val onClickHatenaAuthStatusEventSubject = PublishSubject.create<Unit>()
    val onClickHatenaAuthStatus: Observable<Unit> = onClickHatenaAuthStatusEventSubject

    private var showEditHatenaIdDialogEventSubject = PublishSubject.create<Unit>()
    val showEditHatenaIdDialogEvent: Observable<Unit> = showEditHatenaIdDialogEventSubject

    private var startAuthoriseTwitterEventSubject = PublishSubject.create<Unit>()
    val startAuthoriseTwitterEvent: Observable<Unit> = startAuthoriseTwitterEventSubject

    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.addAll(userModel.user.subscribe {
            userId.set(it.id)
        })
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickHatenaId(view: View) {
        showEditHatenaIdDialogEventSubject.onNext(Unit)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickHatenaAuthStatus(view: View) {
        onClickHatenaAuthStatusEventSubject.onNext(Unit)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickTwitterAuthStatus(view: View) {
        startAuthoriseTwitterEventSubject.onNext(Unit)
    }

    class Factory(private val userModel: UserModel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingFragmentViewModel::class.java)) {
                return SettingFragmentViewModel(userModel) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
