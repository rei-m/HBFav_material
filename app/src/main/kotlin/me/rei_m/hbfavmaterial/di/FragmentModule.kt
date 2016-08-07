package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.fragment.BaseFragment
import me.rei_m.hbfavmaterial.fragment.presenter.BookmarkedUsersContact
import me.rei_m.hbfavmaterial.fragment.presenter.BookmarkedUsersPresenter

@Module
class FragmentModule(val fragment: BaseFragment) {

    @Provides
    fun provideBookmarkedUsersPresenter(): BookmarkedUsersContact.Actions {
        return BookmarkedUsersPresenter()
    }
}
