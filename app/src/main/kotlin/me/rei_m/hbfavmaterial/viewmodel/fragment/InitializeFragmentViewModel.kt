package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.app.ProgressDialog
import android.databinding.ObservableField
import android.view.View
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class InitializeFragmentViewModel(private val userModel: UserModel,
                                  private val navigator: Navigator,
                                  private val progressDialog: ProgressDialog,
                                  private val userIdErrorMessage: String) : AbsFragmentViewModel() {

    val userId: ObservableField<String> = ObservableField("")

    val idErrorMessage: ObservableField<String> = ObservableField("")

    private var completeSetUpEventSubject = PublishSubject.create<Unit>()
    val completeSetUpEvent: io.reactivex.Observable<Unit> = completeSetUpEventSubject

    private var snackbarFactory: SnackbarFactory? = null

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.userUpdatedEvent.subscribe {
            idErrorMessage.set("")
            completeSetUpEventSubject.onNext(Unit)
            progressDialog.dismiss()
            navigator.navigateToMain()
        }, userModel.unauthorisedEvent.subscribe {
            progressDialog.dismiss()
            idErrorMessage.set(userIdErrorMessage)
        }, userModel.error.subscribe {
            progressDialog.dismiss()
            snackbarFactory?.create(R.string.message_error_network)?.show()
        })
    }

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

    override fun onResume() {
        super.onResume()
        if (userModel.user.isCompleteSetting) {
            navigator.navigateToMain()
            completeSetUpEventSubject.onNext(Unit)
        }
    }

    fun onDestroyView() {
        snackbarFactory = null
    }

    fun onClickButtonSetId(view: View) {
        progressDialog.show()
        userModel.setUpUserId(userId.get())
    }
}
