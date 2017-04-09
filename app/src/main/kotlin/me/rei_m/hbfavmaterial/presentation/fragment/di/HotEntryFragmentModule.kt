package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.model.HotEntryModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.HotEntryFragmentViewModel

@Module
class HotEntryFragmentModule {
    @Provides
    fun provideHotEntryFragmentViewModel(hotEntryModel: HotEntryModel,
                                         navigator: Navigator): HotEntryFragmentViewModel {
        return HotEntryFragmentViewModel(hotEntryModel,
                navigator)
    }
}
