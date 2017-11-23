package me.rei_m.hbfavmaterial.viewmodel.widget.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.widget.adapter.UserListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.UserListItemViewModel

@Module
class UserListItemViewModelModule {
    @Provides
    internal fun provideItemInjector(): UserListAdapter.Injector {
        return object : UserListAdapter.Injector {
            override fun bookmarkListItemViewModel(): UserListItemViewModel = UserListItemViewModel()
        }
    }
}
