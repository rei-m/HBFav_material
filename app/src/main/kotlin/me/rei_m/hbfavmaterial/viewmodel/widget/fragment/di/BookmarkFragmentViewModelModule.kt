package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.BookmarkFragmentViewModel

@Module
class BookmarkFragmentViewModelModule(private val bookmarkEntity: BookmarkEntity) {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(): BookmarkFragmentViewModel.Factory =
            BookmarkFragmentViewModel.Factory(bookmarkEntity)
}
