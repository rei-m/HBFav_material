package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogContact
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.usecase.*

@Module
class EditBookmarkDialogFragmentModule(private val context: Context) {
    @Provides
    fun provideEditBookmarkDialogPresenter(getUserUsecase: GetUserUsecase,
                                           getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                           updateUserUsecase: UpdateUserUsecase,
                                           updateTwitterSessionUsecase: UpdateTwitterSessionUsecase,
                                           registerBookmarkUsecase: RegisterBookmarkUsecase,
                                           deleteBookmarkUsecase: DeleteBookmarkUsecase): EditBookmarkDialogContact.Actions {
        return EditBookmarkDialogPresenter(getUserUsecase,
                getTwitterSessionUsecase,
                updateUserUsecase,
                updateTwitterSessionUsecase,
                registerBookmarkUsecase,
                deleteBookmarkUsecase)
    }
}
