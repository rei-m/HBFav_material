package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.models.HatenaModel
import me.rei_m.hbfavmaterial.models.TwitterModel
import me.rei_m.hbfavmaterial.repositories.HatenaRepository
import javax.inject.Singleton

@Module
class AppLayerModule() {

    @Provides
    @Singleton
    fun provideHatenaModel(@ForApplication context: Context, hatenaRepository: HatenaRepository): HatenaModel {
        return HatenaModel(context, hatenaRepository)
    }

    @Provides
    @Singleton
    fun provideTwitterModel(@ForApplication context: Context): TwitterModel {
        return TwitterModel(context)
    }
}
