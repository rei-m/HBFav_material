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
import android.databinding.ObservableField
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import me.rei_m.hbfavmaterial.model.UserModel

class InitializeFragmentViewModel(private val userModel: UserModel,
                                  private val userIdErrorMessage: String,
                                  userId: String) : ViewModel() {

    val userId: ObservableField<String> = ObservableField(userId)

    val idErrorMessage: ObservableField<String> = ObservableField("")

    private var completeSetUpEventSubject = BehaviorSubject.create<Unit>()
    val completeSetUpEvent: io.reactivex.Observable<Unit> = completeSetUpEventSubject

    val isLoading = userModel.isLoading
    val isRaisedError = userModel.isRaisedError

    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.addAll(userModel.user.subscribe {
            if (it.isCompleteSetting) {
                idErrorMessage.set("")
                completeSetUpEventSubject.onNext(Unit)
                return@subscribe
            }
        }, userModel.unauthorised.subscribe {
            idErrorMessage.set(userIdErrorMessage)
        })
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickButtonSetId(view: View) {
        userModel.setUpUserId(userId.get())
    }

    class Factory(private val userModel: UserModel,
                  private val userIdErrorMessage: String,
                  var userId: String = "") : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InitializeFragmentViewModel::class.java)) {
                return InitializeFragmentViewModel(userModel, userIdErrorMessage, userId) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
