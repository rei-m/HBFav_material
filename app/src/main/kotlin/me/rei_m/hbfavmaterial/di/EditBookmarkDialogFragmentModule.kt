package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.domain.service.TwitterService
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.EditBookmarkDialogFragmentViewModel
import me.rei_m.hbfavmaterial.usecase.impl.DeleteBookmarkUsecaseImpl
import me.rei_m.hbfavmaterial.usecase.impl.RegisterBookmarkUsecaseImpl

@Module
class EditBookmarkDialogFragmentModule(private val context: Context) {

    @Provides
    fun provideEditBookmarkDialogFragmentViewModel(userRepository: UserRepository,
                                                   hatenaTokenRepository: HatenaTokenRepository,
                                                   hatenaService: HatenaService,
                                                   twitterService: TwitterService,
                                                   twitterSessionRepository: TwitterSessionRepository,
                                                   rxBus: RxBus,
                                                   navigator: ActivityNavigator): EditBookmarkDialogFragmentViewModel {
        val registerBookmarkUsecase = RegisterBookmarkUsecaseImpl(userRepository,
                hatenaTokenRepository,
                hatenaService,
                twitterService,
                twitterSessionRepository)

        val deleteBookmarkUsecase = DeleteBookmarkUsecaseImpl(hatenaTokenRepository,
                hatenaService)

        return EditBookmarkDialogFragmentViewModel(userRepository,
                twitterSessionRepository,
                registerBookmarkUsecase,
                deleteBookmarkUsecase,
                rxBus,
                navigator)
    }
}
