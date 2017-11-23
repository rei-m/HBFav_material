package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.NewEntryModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.NewEntryFragmentViewModel

@Module
class NewEntryFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(newEntryModel: NewEntryModel,
                                  navigator: Navigator): NewEntryFragmentViewModel =
            NewEntryFragmentViewModel(newEntryModel, navigator)
}
