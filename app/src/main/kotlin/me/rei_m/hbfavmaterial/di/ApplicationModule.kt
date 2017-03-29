package me.rei_m.hbfavmaterial.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideRxBus(): RxBus {
        return RxBus()
    }
}
