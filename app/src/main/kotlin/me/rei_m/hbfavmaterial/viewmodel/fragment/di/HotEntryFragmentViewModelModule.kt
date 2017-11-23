package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.HotEntryModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.HotEntryFragmentViewModel

@Module
class HotEntryFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(hotEntryModel: HotEntryModel,
                                  navigator: Navigator): HotEntryFragmentViewModel =
            HotEntryFragmentViewModel(hotEntryModel, navigator)
}
