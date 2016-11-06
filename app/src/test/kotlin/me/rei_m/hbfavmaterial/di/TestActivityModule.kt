//package me.rei_m.hbfavmaterial.di
//
//import dagger.Module
//import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogContact
//import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogContact
//import me.rei_m.hbfavmaterial.usecase.*
//import org.mockito.Mockito.mock
//
//@Module
//class TestActivityModule : ActivityModule() {
//
//    override fun createEditBookmarkDialogPresenter(getUserUsecase: GetUserUsecase,
//                                                   getTwitterSessionUsecase: GetTwitterSessionUsecase,
//                                                   updateUserUsecase: UpdateUserUsecase,
//                                                   updateTwitterSessionUsecase: UpdateTwitterSessionUsecase,
//                                                   registerBookmarkUsecase: RegisterBookmarkUsecase,
//                                                   deleteBookmarkUsecase: DeleteBookmarkUsecase): EditBookmarkDialogContact.Actions {
//        return mock(EditBookmarkDialogContact.Actions::class.java)
//    }
//
//    override fun createEditUserIdDialogPresenter(getUserUsecase: GetUserUsecase,
//                                                 confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase): EditUserIdDialogContact.Actions {
//        return mock(EditUserIdDialogContact.Actions::class.java)
//    }
//}
