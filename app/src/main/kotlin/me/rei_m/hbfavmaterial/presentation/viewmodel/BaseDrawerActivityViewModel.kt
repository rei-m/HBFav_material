package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.extension.subscribeBus
import me.rei_m.hbfavmaterial.presentation.event.FinishActivityEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.UpdateHatenaIdEvent
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdapter

class BaseDrawerActivityViewModel(private val userRepository: UserRepository,
                                  private val rxBus: RxBus,
                                  private val navigator: ActivityNavigator) : AbsActivityViewModel() {

    val userId: ObservableField<String> = ObservableField()

    val checkedNavId: ObservableInt = ObservableInt()

    val currentItem: ObservableInt = ObservableInt()

    val isVisiblePager: ObservableBoolean = ObservableBoolean(true)

    override fun onResume() {
        super.onResume()
        userId.set(userRepository.resolve().id)
        registerDisposable(rxBus.toObservable().subscribeBus({
            when (it) {
                is UpdateHatenaIdEvent -> {
                    userId.set(it.userId)
                }
            }
        }))
    }

    fun onNavigationMainSelected(page: BookmarkPagerAdapter.Page) {
        navigator.navigateToMain(page)
        rxBus.send(FinishActivityEvent())
    }

    fun onNavigationSettingSelected() {
        navigator.navigateToSetting()
        rxBus.send(FinishActivityEvent())
    }

    fun onNavigationExplainAppSelected() {
        navigator.navigateToExplainApp()
        rxBus.send(FinishActivityEvent())
    }

    fun onNavigationPageSelected(page: BookmarkPagerAdapter.Page) {
        currentItem.set(page.index)
        checkedNavId.set(page.navId)
    }

    fun onPageSelected(checkedNavId: Int) {
        if (checkedNavId == R.id.nav_explain_app || checkedNavId == R.id.nav_setting) {
            isVisiblePager.set(false)
        }
    }
}
