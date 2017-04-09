package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.app.ProgressDialog
import android.databinding.ObservableField
import android.view.View
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class EditUserIdDialogFragmentViewModel(private val userModel: UserModel,
                                        private val progressDialog: ProgressDialog,
                                        private val userIdErrorMessage: String) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    private var dismissDialogEventSubject = PublishSubject.create<Unit>()
    val dismissDialogEvent: io.reactivex.Observable<Unit> = dismissDialogEventSubject

    private var snackbarFactory: SnackbarFactory? = null

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.userUpdatedEvent.subscribe {
            progressDialog.dismiss()
            idErrorMessage.set("")
            userId.set(it.id)
            dismissDialogEventSubject.onNext(Unit)
        }, userModel.unauthorisedEvent.subscribe {
            progressDialog.dismiss()
            idErrorMessage.set(userIdErrorMessage)
        }, userModel.error.subscribe {
            progressDialog.dismiss()
            snackbarFactory?.create(R.string.message_error_network)?.show()
        })
    }

    override fun onResume() {
        super.onResume()
        userId.set(userModel.user.id)
    }

    fun onDestroyView() {
        snackbarFactory = null
    }

    fun onClickSetUp(view: View) {
        progressDialog.show()
        userModel.setUpUserId(userId.get())
    }

    fun onClickCancel(view: View) {
        dismissDialogEventSubject.onNext(Unit)
    }
}
