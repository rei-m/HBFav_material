package me.rei_m.hbfavmaterial.presentation.fragment.di

import android.app.ProgressDialog
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditUserIdDialogFragmentViewModel

@Module
class EditUserIdDialogFragmentModule {

    @Provides
    fun provideEditUserIdDialogFragmentViewModel(context: Context,
                                                 userModel: UserModel,
                                                 progressDialog: ProgressDialog): EditUserIdDialogFragmentViewModel {
        return EditUserIdDialogFragmentViewModel(userModel,
                progressDialog,
                context.getString(R.string.message_error_input_user_id))
    }
}
