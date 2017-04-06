package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.model.FavoriteBookmarkModel
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.ReadAllListItemEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class FavoriteBookmarkFragmentViewModel(private val favoriteBookmarkModel: FavoriteBookmarkModel,
                                        private val userModel: UserModel,
                                        private val rxBus: RxBus,
                                        private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    override fun onStart() {
        super.onStart()
        registerDisposable(favoriteBookmarkModel.bookmarkList.subscribe {
            bookmarkList.clear()
            bookmarkList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
        }, favoriteBookmarkModel.hasNextPage.subscribe {
            if (!it) {
                rxBus.send(ReadAllListItemEvent())
            }
        }, favoriteBookmarkModel.error.subscribe {
            rxBus.send(FailToConnectionEvent())
        }, userModel.user.subscribe {
            if (favoriteBookmarkModel.userId != it.id) {
                favoriteBookmarkModel.userId = it.id
                favoriteBookmarkModel.getList()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (bookmarkList.isEmpty()) {
            isVisibleProgress.set(true)
            userModel.getUser()
        }
    }

    override fun onPause() {
        super.onPause()
        isVisibleProgress.set(false)
        isRefreshing.set(false)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigator.navigateToBookmark(bookmarkList[position])
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScroll(listView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
            favoriteBookmarkModel.getNextPage()
        }
    }

    fun onRefresh() {
        isRefreshing.set(true)
        favoriteBookmarkModel.getList()
    }
}
