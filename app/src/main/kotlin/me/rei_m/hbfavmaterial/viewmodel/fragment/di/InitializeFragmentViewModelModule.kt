package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import android.app.ProgressDialog
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.InitializeFragmentViewModel

@Module
class InitializeFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(context: Context,
                                  userModel: UserModel,
                                  navigator: Navigator,
                                  progressDialog: ProgressDialog): InitializeFragmentViewModel =
            InitializeFragmentViewModel(userModel, navigator, progressDialog, context.getString(R.string.message_error_input_user_id))
}
