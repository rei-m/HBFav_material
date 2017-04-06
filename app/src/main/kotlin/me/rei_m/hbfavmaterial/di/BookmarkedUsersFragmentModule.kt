package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.BookmarkModel
import me.rei_m.hbfavmaterial.infra.network.HatenaApiService
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkedUsersFragmentViewModel

@Module
open class BookmarkedUsersFragmentModule(fragment: Fragment) {
    @Provides
    fun provideBookmarkedUsersFragmentViewModel(rxBus: RxBus,
                                                navigator: Navigator): BookmarkedUsersFragmentViewModel {
        val hatenaApiService = RetrofitManager.json.create(HatenaApiService::class.java)
        return BookmarkedUsersFragmentViewModel(BookmarkModel(hatenaApiService),
                rxBus,
                navigator)
    }
}
