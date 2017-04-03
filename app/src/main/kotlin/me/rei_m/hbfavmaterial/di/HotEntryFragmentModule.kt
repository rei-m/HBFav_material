package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.EntryRepository
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.HotEntryFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.GetHotEntriesUsecaseImpl

@Module
class HotEntryFragmentModule(fragment: Fragment) {
    @Provides
    fun provideHotEntryFragmentViewModel(entryRepository: EntryRepository,
                                         rxBus: RxBus,
                                         navigator: ActivityNavigator): HotEntryFragmentViewModel {
        return HotEntryFragmentViewModel(GetHotEntriesUsecaseImpl(entryRepository),
                rxBus,
                navigator)
    }
}
