package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.HotEntryModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.HotEntryFragmentViewModel

@Module
class HotEntryFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(hotEntryModel: HotEntryModel): HotEntryFragmentViewModel.Factory =
            HotEntryFragmentViewModel.Factory(hotEntryModel)
}
