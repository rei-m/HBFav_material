package me.rei_m.hbfavmaterial.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BaseDrawerActivityViewModel

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun provideActivityNavigator(): Navigator {
        return Navigator(activity)
    }

    @Provides
    fun provideBaseDrawerActivityViewModel(userModel: UserModel,
                                           rxBus: RxBus,
                                           navigator: Navigator): BaseDrawerActivityViewModel {
        return BaseDrawerActivityViewModel(userModel,
                rxBus,
                navigator)
    }
}
