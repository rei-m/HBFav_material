package me.rei_m.hbfavmaterial.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.fragment.BaseFragment
import me.rei_m.hbfavmaterial.fragment.presenter.*

@Module
class FragmentModule(val fragment: BaseFragment) {

    @Provides
    fun provideBookmarkedUsersPresenter(): BookmarkedUsersContact.Actions {
        return BookmarkedUsersPresenter()
    }

    @Provides
    fun provideBookmarkFavoritePresenter(): BookmarkFavoriteContact.Actions {
        return BookmarkFavoritePresenter()
    }

    @Provides
    fun provideBookmarkUserPresenter(): BookmarkUserContact.Actions {
        return BookmarkUserPresenter()
    }
}
