package me.rei_m.hbfavmaterial.viewmodel.widget.fragment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import me.rei_m.hbfavmaterial.model.UserModel

class InitializeFragmentViewModel(private val userModel: UserModel,
                                  private val userIdErrorMessage: String) : ViewModel() {

    val userId: ObservableField<String> = ObservableField("")

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
                  private val userIdErrorMessage: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InitializeFragmentViewModel::class.java)) {
                return InitializeFragmentViewModel(userModel, userIdErrorMessage) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
