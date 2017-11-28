package me.rei_m.hbfavmaterial.viewmodel.widget.dialog.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.EditUserIdDialogFragmentViewModel

@Module
class EditUserIdDialogFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(context: Context,
                                         userModel: UserModel): EditUserIdDialogFragmentViewModel.Factory =
            EditUserIdDialogFragmentViewModel.Factory(userModel, context.getString(R.string.message_error_input_user_id))
}
