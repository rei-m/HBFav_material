package me.rei_m.hbfavmaterial.presentation.activity.di

import android.app.Activity
import android.app.ProgressDialog
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    fun provideActivity(): Activity = activity

    @Provides
    fun provideActivityNavigator(): Navigator = Navigator(activity)

    @Provides
    fun provideProgressDialog(): ProgressDialog = ProgressDialog(activity)
}
