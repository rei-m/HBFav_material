package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryContact
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryPresenter
import me.rei_m.hbfavmaterial.usecase.GetHotEntriesUsecase

@Module
class HotEntryFragmentModule(private val context: Context) {
    @Provides
    fun provideHotEntryPresenter(getHotEntriesUsecase: GetHotEntriesUsecase): HotEntryContact.Actions {
        return HotEntryPresenter(getHotEntriesUsecase)
    }
}
