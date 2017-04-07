package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.application.TwitterService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.viewmodel.fragment.EditBookmarkDialogFragmentViewModel

@Module
class EditBookmarkDialogFragmentModule {

    @Provides
    fun provideEditBookmarkDialogFragmentViewModel(hatenaService: HatenaService,
                                                   twitterService: TwitterService,
                                                   rxBus: RxBus,
                                                   navigator: Navigator): EditBookmarkDialogFragmentViewModel {

        return EditBookmarkDialogFragmentViewModel(hatenaService,
                twitterService,
                rxBus,
                navigator)
    }
}
