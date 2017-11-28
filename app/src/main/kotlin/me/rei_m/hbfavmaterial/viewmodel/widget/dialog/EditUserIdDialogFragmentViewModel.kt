package me.rei_m.hbfavmaterial.viewmodel.widget.dialog

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.model.UserModel

class EditUserIdDialogFragmentViewModel(private val userModel: UserModel,
                                        private val userIdErrorMessage: String) : ViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    val isLoading = userModel.isLoading
    val isRaisedError = userModel.isRaisedError

    private var dismissDialogEventSubject = PublishSubject.create<Unit>()
    val dismissDialogEvent: io.reactivex.Observable<Unit> = dismissDialogEventSubject

    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.addAll(userModel.user.subscribe {
            if (userId.get() == "") {
                userId.set(it.id)
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
                  private val userIdErrorMessage: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditUserIdDialogFragmentViewModel::class.java)) {
                return EditUserIdDialogFragmentViewModel(userModel, userIdErrorMessage) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
