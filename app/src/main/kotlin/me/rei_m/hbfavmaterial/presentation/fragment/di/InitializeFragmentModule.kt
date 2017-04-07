package me.rei_m.hbfavmaterial.presentation.fragment.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.InitializeFragmentViewModel

@Module
class InitializeFragmentModule {
    @Provides
    fun provideInitializeFragmentViewModel(context: Context,
                                           userModel: UserModel,
                                           rxBus: RxBus,
                                           navigator: Navigator): InitializeFragmentViewModel {
        return InitializeFragmentViewModel(userModel,
                rxBus,
                navigator,
                context.getString(R.string.message_error_input_user_id))
    }
}
