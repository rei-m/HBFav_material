package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryContact
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryPresenter
import me.rei_m.hbfavmaterial.usecase.GetNewEntriesUsecase

@Module
class NewEntryFragmentModule(private val context: Context) {
    @Provides
    fun provideNewEntryPresenter(getNewEntriesUsecase: GetNewEntriesUsecase): NewEntryContact.Actions {
        return NewEntryPresenter(getNewEntriesUsecase)
    }
}
