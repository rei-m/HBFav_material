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
        registerDisposable(userBookmarkModel.bookmarkListUpdatedEvent.subscribe {
            bookmarkList.clear()
            bookmarkList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
            rxBus.send(UpdateMainPageFilterEvent())
        }, userBookmarkModel.hasNextPageUpdatedEvent.subscribe {
            if (!it) {
                rxBus.send(ReadAllListItemEvent())
            }
        }, userBookmarkModel.readAfterFilterUpdatedEvent.subscribe {
            readAfterFilter = it
        }, userBookmarkModel.error.subscribe {
            rxBus.send(FailToConnectionEvent())
        })
    }

    override fun onResume() {
        super.onResume()
        if (bookmarkList.isEmpty()) {
            isVisibleProgress.set(true)
            if (isOwner) {
                userBookmarkModel.getList(userModel.user.id, readAfterFilter)
            } else {
                userBookmarkModel.getList(bookmarkUserId, readAfterFilter)
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
            if (userBookmarkModel.hasNextPage) {
                userBookmarkModel.getNextPage()
            }
        }
    }

    fun onRefresh() {
        isRefreshing.set(true)
        if (isOwner) {
            userBookmarkModel.getList(userModel.user.id, readAfterFilter)
        } else {
            userBookmarkModel.getList(bookmarkUserId, readAfterFilter)
        }
    }

    fun onOptionItemSelected(readAfterFilter: ReadAfterFilter) {
        if (this.readAfterFilter == readAfterFilter) return
        isVisibleProgress.set(true)
        if (isOwner) {
            userBookmarkModel.getList(userModel.user.id, readAfterFilter)
        } else {
            userBookmarkModel.getList(bookmarkUserId, readAfterFilter)
        }
    }
}
