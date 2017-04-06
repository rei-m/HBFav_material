package me.rei_m.hbfavmaterial.di

import android.content.Context
import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.InitializeFragmentViewModel

@Module
class InitializeFragmentModule(fragment: Fragment) {
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
