package me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.OthersBookmarkFragmentViewModel
import javax.inject.Named

@Module
class OthersBookmarkFragmentViewModelModule(private val userId: String) {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(@Named("othersUserBookmarkModel") userBookmarkModel: UserBookmarkModel): OthersBookmarkFragmentViewModel.Factory =
            OthersBookmarkFragmentViewModel.Factory(userBookmarkModel, userId)
}
