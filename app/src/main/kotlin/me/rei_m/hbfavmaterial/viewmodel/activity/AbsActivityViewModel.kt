package me.rei_m.hbfavmaterial.viewmodel.activity

import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class AbsActivityViewModel {

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
    }

    @CallSuper
    open fun onStop() {
        disposable?.dispose()
        disposable = null
    }

    fun registerDisposable(vararg d: Disposable) {
        disposable?.addAll(*d)
    }
}
