package me.rei_m.hbfavmaterial.viewmodel.widget.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.widget.adapter.EntryListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.EntryListItemViewModel

@Module
class EntryListItemViewModelModule {
    @Provides
    internal fun provideItemInjector(): EntryListAdapter.Injector {
        return object : EntryListAdapter.Injector {
            override fun entryListItemViewModel(): EntryListItemViewModel = EntryListItemViewModel()
        }
    }
}
