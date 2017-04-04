package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.domain.service.TwitterService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.EditBookmarkDialogFragmentViewModel

@Module
class EditBookmarkDialogFragmentModule(private val context: Context) {

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
