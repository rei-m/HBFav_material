package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.FavoriteBookmarkModel
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.FavoriteBookmarkFragmentViewModel

@Module
class BookmarkFavoriteFragmentModule(fragment: Fragment) {

    @Provides
    fun provideBookmarkFavoriteViewModel(userModel: UserModel,
                                         rxBus: RxBus,
                                         navigator: Navigator): FavoriteBookmarkFragmentViewModel {
        return FavoriteBookmarkFragmentViewModel(FavoriteBookmarkModel(RetrofitManager.xml.create(HatenaRssService::class.java)),
                userModel,
                rxBus,
                navigator)
    }
}
