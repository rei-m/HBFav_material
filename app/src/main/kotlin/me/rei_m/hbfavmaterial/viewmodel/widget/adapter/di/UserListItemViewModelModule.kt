package me.rei_m.hbfavmaterial.viewmodel.widget.adapter.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.widget.adapter.UserListAdapter
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.UserListItemViewModel

@Module
class UserListItemViewModelModule {
    @Provides
    internal fun provideItemInjector(): UserListAdapter.Injector {
        return object : UserListAdapter.Injector {
            override fun userListItemViewModel(): UserListItemViewModel = UserListItemViewModel()
        }
    }
}
