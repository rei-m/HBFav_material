package me.rei_m.hbfavmaterial.viewmodel.fragment.di

import android.app.ProgressDialog
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditBookmarkDialogFragmentViewModel

@Module
class EditBookmarkDialogFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModel(hatenaService: HatenaService,
                                  twitterService: TwitterService,
                                  navigator: Navigator,
                                  progressDialog: ProgressDialog): EditBookmarkDialogFragmentViewModel =
            EditBookmarkDialogFragmentViewModel(hatenaService, twitterService, navigator, progressDialog)
}
