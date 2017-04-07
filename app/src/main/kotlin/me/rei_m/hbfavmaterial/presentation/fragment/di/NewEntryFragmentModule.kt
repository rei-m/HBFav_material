package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.model.NewEntryModel
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.NewEntryFragmentViewModel

@Module
class NewEntryFragmentModule {
    @Provides
    fun provideHotEntryFragmentViewModel(newEntryModel: NewEntryModel,
                                         rxBus: RxBus,
                                         navigator: Navigator): NewEntryFragmentViewModel {
        return NewEntryFragmentViewModel(newEntryModel,
                rxBus,
                navigator)
    }
}
