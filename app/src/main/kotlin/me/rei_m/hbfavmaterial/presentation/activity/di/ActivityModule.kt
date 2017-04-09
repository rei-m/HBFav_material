package me.rei_m.hbfavmaterial.presentation.activity.di

import android.app.Activity
import android.app.ProgressDialog
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel

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
    fun provideProgressDialog(): ProgressDialog {
        return ProgressDialog(activity)
    }

    @Provides
    fun provideBaseDrawerActivityViewModel(userModel: UserModel,
                                           navigator: Navigator): BaseDrawerActivityViewModel {
        return BaseDrawerActivityViewModel(userModel,
                navigator)
    }
}
