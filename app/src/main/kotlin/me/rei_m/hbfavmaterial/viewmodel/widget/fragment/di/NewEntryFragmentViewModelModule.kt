package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.NewEntryModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.NewEntryFragmentViewModel

@Module
class NewEntryFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(newEntryModel: NewEntryModel): NewEntryFragmentViewModel.Factory =
            NewEntryFragmentViewModel.Factory(newEntryModel)
}
