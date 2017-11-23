package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.BookmarkFragmentViewModel

@Module
class BookmarkFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(navigator: Navigator): BookmarkFragmentViewModel =
            BookmarkFragmentViewModel(navigator)
}
