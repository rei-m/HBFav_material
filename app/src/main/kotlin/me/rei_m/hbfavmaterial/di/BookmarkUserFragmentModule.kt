package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.UserBookmarkFragmentViewModel

@Module
class BookmarkUserFragmentModule(fragment: Fragment) {

    @Provides
    fun provideBookmarkUserViewModel(userModel: UserModel,
                                     rxBus: RxBus,
                                     navigator: Navigator): UserBookmarkFragmentViewModel {
        return UserBookmarkFragmentViewModel(UserBookmarkModel(RetrofitManager.xml.create(HatenaRssService::class.java)),
                userModel,
                rxBus,
                navigator)
    }
}
