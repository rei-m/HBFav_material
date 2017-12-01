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

package me.rei_m.hbfavmaterial.viewmodel.widget.dialog

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.model.UserModel

class EditUserIdDialogFragmentViewModel(private val userModel: UserModel,
                                        private val userIdErrorMessage: String,
                                        userId: String) : ViewModel() {

    val userId: ObservableField<String> = ObservableField(userId)

    val idErrorMessage: ObservableField<String> = ObservableField("")

    val isLoading = userModel.isLoading
    val isRaisedError = userModel.isRaisedError

    private var dismissDialogEventSubject = PublishSubject.create<Unit>()
    val dismissDialogEvent: io.reactivex.Observable<Unit> = dismissDialogEventSubject

    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.addAll(userModel.user.subscribe {
            if (this.userId.get() == "") {
                this.userId.set(it.id)
            } else {
                idErrorMessage.set("")
                dismissDialogEventSubject.onNext(Unit)
            }
        }, userModel.unauthorised.subscribe {
            idErrorMessage.set(userIdErrorMessage)
        })
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickSetUp(view: View) {
        userModel.setUpUserId(userId.get())
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickCancel(view: View) {
        dismissDialogEventSubject.onNext(Unit)
    }

    class Factory(private val userModel: UserModel,
                  private val userIdErrorMessage: String,
                  var userId: String = "") : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditUserIdDialogFragmentViewModel::class.java)) {
                return EditUserIdDialogFragmentViewModel(userModel, userIdErrorMessage, userId) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
