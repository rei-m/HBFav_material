package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.FinishActivityEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter

class BaseDrawerActivityViewModel(private val userModel: UserModel,
                                  private val rxBus: RxBus,
                                  private val navigator: Navigator) : AbsActivityViewModel() {

    val userId: ObservableField<String> = ObservableField()

    val checkedNavId: ObservableInt = ObservableInt()

    val currentItem: ObservableInt = ObservableInt()

    val isVisiblePager: ObservableBoolean = ObservableBoolean(true)

    override fun onStart() {
        super.onStart()
        registerDisposable(userModel.userUpdatedEvent.subscribe {
            userId.set(it.id)
        })
    }

    override fun onResume() {
        super.onResume()
        userId.set(userModel.user.id)
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
