package me.rei_m.hbfavmaterial.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    /**
     * Allow the application context to be injected
     * but require that it be annotated with [ ][ForApplication] to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideActivityNavigator(): ActivityNavigator {
        return ActivityNavigator()
    }
}
