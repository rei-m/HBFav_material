package me.rei_m.hbfavmaterial.viewmodel.activity

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter

class BaseDrawerActivityViewModel(private val userModel: UserModel,
                                  private val navigator: Navigator) : AbsActivityViewModel() {

    val userId: ObservableField<String> = ObservableField()

    val checkedNavId: ObservableInt = ObservableInt()

    val currentItem: ObservableInt = ObservableInt()

    val isVisiblePager: ObservableBoolean = ObservableBoolean(true)

    private var movePageEventSubject = PublishSubject.create<Unit>()
    val movePageEvent: io.reactivex.Observable<Unit> = movePageEventSubject

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
        movePageEventSubject.onNext(Unit)
    }

    fun onNavigationSettingSelected() {
        navigator.navigateToSetting()
        movePageEventSubject.onNext(Unit)
    }

    fun onNavigationExplainAppSelected() {
        navigator.navigateToExplainApp()
        movePageEventSubject.onNext(Unit)
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
