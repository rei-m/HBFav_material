package me.rei_m.hbfavmaterial.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BaseDrawerActivityViewModel

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun provideActivityNavigator(): ActivityNavigator {
        return ActivityNavigator(activity)
    }

    @Provides
    fun provideBaseDrawerActivityViewModel(userRepository: UserRepository,
                                           rxBus: RxBus,
                                           navigator: ActivityNavigator): BaseDrawerActivityViewModel {
        return BaseDrawerActivityViewModel(userRepository,
                rxBus,
                navigator)
    }
}
