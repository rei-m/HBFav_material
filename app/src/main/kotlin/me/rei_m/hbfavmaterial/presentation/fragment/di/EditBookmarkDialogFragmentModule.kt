package me.rei_m.hbfavmaterial.presentation.fragment.di

import android.app.ProgressDialog
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditBookmarkDialogFragmentViewModel

@Module
class EditBookmarkDialogFragmentModule {

    @Provides
    fun provideEditBookmarkDialogFragmentViewModel(hatenaService: HatenaService,
                                                   twitterService: TwitterService,
                                                   navigator: Navigator,
                                                   progressDialog: ProgressDialog): EditBookmarkDialogFragmentViewModel {

        return EditBookmarkDialogFragmentViewModel(hatenaService,
                twitterService,
                navigator,
                progressDialog)
    }
}
