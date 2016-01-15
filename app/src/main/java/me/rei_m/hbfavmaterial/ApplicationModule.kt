package me.rei_m.hbfavmaterial

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * A module for Android-specific dependencies which require a [android.content.Context] or [ ] to create.
 */
@Module
class ApplicationModule(private val application: Application) {

    /**
     * Allow the application context to be injected
     * but require that it be annotated with [ ][ForApplication] to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    fun provideApplicationContext(): Context {
        return application
    }



    //    @Provides
    //    @Singleton
    //    fun provideModelLocator(): ModelLocator {
    //        return application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    //    }
}
