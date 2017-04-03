package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkFragmentViewModel

@Module
class BookmarkFragmentModule(fragment: Fragment) {

    @Provides
    fun provideBookmarkViewModel(rxBus: RxBus,
                                 navigator: ActivityNavigator): BookmarkFragmentViewModel {
        return BookmarkFragmentViewModel(rxBus, navigator)
    }
}
