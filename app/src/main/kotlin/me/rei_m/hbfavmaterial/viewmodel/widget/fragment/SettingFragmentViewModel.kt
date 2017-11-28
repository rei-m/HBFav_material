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
