package me.rei_m.hbfavmaterial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogContact
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogPresenter
import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase

@Module
class EditUserIdDialogFragmentModule(private val context: Context) {
    @Provides
    fun provideEditUserIdDialogPresenter(getUserUsecase: GetUserUsecase,
                                         confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): EditUserIdDialogContact.Actions {
        return EditUserIdDialogPresenter(getUserUsecase, confirmExistingUserIdUsecase)
    }
}
