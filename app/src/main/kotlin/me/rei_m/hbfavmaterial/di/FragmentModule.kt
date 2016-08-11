package me.rei_m.hbfavmaterial.di

import android.content.Context
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

    @Provides
    fun provideHotEntryPresenter(): HotEntryContact.Actions {
        return HotEntryPresenter()
    }

    @Provides
    fun provideNewEntryPresenter(): NewEntryContact.Actions {
        return NewEntryPresenter()
    }

    @Provides
    fun provideInitializePresenter(@ForApplication context: Context): InitializeContact.Actions {
        return InitializePresenter(context)
    }
}
