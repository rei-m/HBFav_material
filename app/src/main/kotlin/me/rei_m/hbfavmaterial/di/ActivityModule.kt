package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogContact
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogContact
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogPresenter
import me.rei_m.hbfavmaterial.usecase.*

@Module
open class ActivityModule() {

    @Provides
    fun provideEditBookmarkDialogPresenter(getUserUsecase: GetUserUsecase,
                                           getTwitterSessionUsecase: GetTwitterSessionUsecase,
                                           updateUserUsecase: UpdateUserUsecase,
                                           updateTwitterSessionUsecase: UpdateTwitterSessionUsecase,
                                           registerBookmarkUsecase: RegisterBookmarkUsecase,
                                           deleteBookmarkUsecase: DeleteBookmarkUsecase): EditBookmarkDialogContact.Actions {

        return createEditBookmarkDialogPresenter(getUserUsecase,
                getTwitterSessionUsecase,
                updateUserUsecase,
                updateTwitterSessionUsecase,
                registerBookmarkUsecase,
                deleteBookmarkUsecase)
    }

    open fun createEditBookmarkDialogPresenter(getUserUsecase: GetUserUsecase,
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

    @Provides
    fun provideEditUserIdDialogPresenter(getUserUsecase: GetUserUsecase,
                                         confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): EditUserIdDialogContact.Actions {
        return EditUserIdDialogPresenter(getUserUsecase, confirmExistingUserIdUsecase)
    }
}
