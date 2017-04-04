package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.domain.model.UserModel
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.ReadAllListItemEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.event.UpdateMainPageFilterEvent
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class UserBookmarkFragmentViewModel(private val userBookmarkModel: UserBookmarkModel,
                                    private val userModel: UserModel,
                                    private val rxBus: RxBus,
                                    private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var isOwner: Boolean = false

    var bookmarkUserId: String = ""

    var readAfterFilter = ReadAfterFilter.ALL

    override fun onStart() {
        super.onStart()
        registerDisposable(userBookmarkModel.bookmarkList.subscribe {
            bookmarkList.clear()
            bookmarkList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
            rxBus.send(UpdateMainPageFilterEvent())
        }, userBookmarkModel.hasNextPage.subscribe {
            if (!it) {
                rxBus.send(ReadAllListItemEvent())
            }
        }, userBookmarkModel.error.subscribe {
            rxBus.send(FailToConnectionEvent())
        }, userBookmarkModel.readAfterFilter.subscribe {
            readAfterFilter = it
        }, userModel.user.subscribe {
            if (userBookmarkModel.userId != it.id) {
                userBookmarkModel.userId = it.id
                userBookmarkModel.getList(readAfterFilter)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (bookmarkList.isEmpty()) {
            isVisibleProgress.set(true)
            if (isOwner) {
                userModel.getUser()
            } else {
                userBookmarkModel.userId = bookmarkUserId
                userBookmarkModel.getList(readAfterFilter)
            }
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
            userBookmarkModel.getNextPage()
        }
    }

    fun onRefresh() {
        isRefreshing.set(true)
        userBookmarkModel.getList(readAfterFilter)
    }

    fun onOptionItemSelected(readAfterFilter: ReadAfterFilter) {
        if (this.readAfterFilter == readAfterFilter) return
        isVisibleProgress.set(true)
        userBookmarkModel.getList(readAfterFilter)
    }
}
