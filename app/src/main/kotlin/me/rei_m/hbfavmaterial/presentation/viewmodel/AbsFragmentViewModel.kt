package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class AbsFragmentViewModel {

    private var disposable: CompositeDisposable? = null
        get() {
            if (field == null) {
                field = CompositeDisposable()
            }
            return field
        }

    @CallSuper
    open fun onStart() {
    }

    @CallSuper
    open fun onResume() {
    }

    @CallSuper
    open fun onPause() {
        disposable?.dispose()
        disposable = null
    }

    @CallSuper
    open fun onStop() {
    }

    fun registerDisposable(vararg d: Disposable) {
        disposable?.addAll(*d)
    }
}
