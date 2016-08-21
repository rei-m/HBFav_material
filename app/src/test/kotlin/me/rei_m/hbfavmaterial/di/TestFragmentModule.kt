package me.rei_m.hbfavmaterial.di

import dagger.Module
import me.rei_m.hbfavmaterial.fragment.presenter.InitializeContact
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService

@Module
class TestFragmentModule : FragmentModule() {
    override fun createInitializePresenter(userRepository: UserRepository,
                                           userService: UserService): InitializeContact.Actions {
        return object : InitializeContact.Actions {
            override fun onCreate(view: InitializeContact.View) {
            }

            override fun onResume() {
            }

            override fun onPause() {
            }

            override fun onClickButtonSetId(userId: String) {
            }
        }
    }
}