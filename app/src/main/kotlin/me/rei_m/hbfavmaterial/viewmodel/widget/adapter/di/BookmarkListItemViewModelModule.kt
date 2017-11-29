package me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.BookmarkListItemViewModel

@Module
class BookmarkListItemViewModelModule {
    @Provides
    internal fun provideItemInjector(): BookmarkListAdapter.Injector {
        return object : BookmarkListAdapter.Injector {
            override fun bookmarkListItemViewModel(): BookmarkListItemViewModel = BookmarkListItemViewModel()
        }
    }
}
