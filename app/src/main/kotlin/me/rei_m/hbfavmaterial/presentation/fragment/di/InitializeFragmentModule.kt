package me.rei_m.hbfavmaterial.presentation.fragment.di

import android.app.ProgressDialog
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.InitializeFragmentViewModel

@Module
class InitializeFragmentModule {
    @Provides
    fun provideInitializeFragmentViewModel(context: Context,
                                           userModel: UserModel,
                                           navigator: Navigator,
                                           progressDialog: ProgressDialog): InitializeFragmentViewModel {
        return InitializeFragmentViewModel(userModel,
                navigator,
                progressDialog,
                context.getString(R.string.message_error_input_user_id))
    }
}
