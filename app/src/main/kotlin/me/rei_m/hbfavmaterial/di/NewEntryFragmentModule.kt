package me.rei_m.hbfavmaterial.di

import android.support.v4.app.Fragment
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.domain.model.NewEntryModel
import me.rei_m.hbfavmaterial.infra.network.HatenaRssService
import me.rei_m.hbfavmaterial.infra.network.RetrofitManager
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.viewmodel.NewEntryFragmentViewModel

@Module
class NewEntryFragmentModule(fragment: Fragment) {
    @Provides
    fun provideHotEntryFragmentViewModel(rxBus: RxBus,
                                         navigator: Navigator): NewEntryFragmentViewModel {

        val hatenaRssService = RetrofitManager.xml.create(HatenaRssService::class.java)

        return NewEntryFragmentViewModel(NewEntryModel(hatenaRssService),
                rxBus,
                navigator)
    }
}
