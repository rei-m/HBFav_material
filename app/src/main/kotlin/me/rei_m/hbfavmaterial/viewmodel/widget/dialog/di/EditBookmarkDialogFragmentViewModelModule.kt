package me.rei_m.hbfavmaterial.viewmodel.widget.dialog.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.EditBookmarkDialogFragmentViewModel

@Module
class EditBookmarkDialogFragmentViewModelModule(private val articleTitle: String,
                                                private val articleUrl: String) {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(hatenaService: HatenaService,
                                         twitterService: TwitterService): EditBookmarkDialogFragmentViewModel.Factory =
            EditBookmarkDialogFragmentViewModel.Factory(articleTitle, articleUrl, hatenaService, twitterService)
}
