package me.rei_m.hbfavmaterial.di

import android.app.Activity
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator

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
}
