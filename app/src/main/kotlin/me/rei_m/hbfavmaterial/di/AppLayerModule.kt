package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.models.TwitterModel
import javax.inject.Singleton

@Module
class AppLayerModule() {

    @Provides
    @Singleton
    fun provideTwitterModel(@ForApplication context: Context): TwitterModel {
        return TwitterModel(context)
    }
}
