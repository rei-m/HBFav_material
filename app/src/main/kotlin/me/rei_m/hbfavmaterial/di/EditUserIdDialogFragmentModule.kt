package me.rei_m.hbfavmaterial.di

import android.content.Context
import android.support.v4.app.DialogFragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.viewmodel.EditUserIdDialogFragmentViewModel

@Module
class EditUserIdDialogFragmentModule(fragment: DialogFragment) {

    @Provides
    fun provideEditUserIdDialogFragmentViewModel(context: Context,
                                                 userModel: UserModel,
                                                 rxBus: RxBus): EditUserIdDialogFragmentViewModel {
        return EditUserIdDialogFragmentViewModel(userModel,
                rxBus,
                context.getString(R.string.message_error_input_user_id))
    }
}
