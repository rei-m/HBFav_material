package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.HotEntryModel
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.HotEntryFragmentViewModel

@Module
class HotEntryFragmentModule(fragment: Fragment) {
    @Provides
    fun provideHotEntryFragmentViewModel(rxBus: RxBus,
                                         navigator: Navigator): HotEntryFragmentViewModel {

        val hatenaRssService = RetrofitManager.xml.create(HatenaRssService::class.java)
        val hotEntryRssService = RetrofitManager.xmlForHotEntryAll.create(HatenaRssService::class.java)

        return HotEntryFragmentViewModel(HotEntryModel(hatenaRssService, hotEntryRssService),
                rxBus,
                navigator)
    }
}
