package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.GetFavoriteBookmarksUsecase

class BookmarkFavoriteFragmentViewModel(private val getFavoriteBookmarksUsecase: GetFavoriteBookmarksUsecase,
                                        private val rxBus: RxBus,
                                        private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    companion object {
        private const val BOOKMARK_COUNT_PER_PAGE = 25
    }

    val bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    val hasListViewFooter: ObservableBoolean = ObservableBoolean(true)

    private var isLoading: Boolean = false

    private val nextIndex: Int
        get() {
            val pageCnt = (bookmarkList.size / BOOKMARK_COUNT_PER_PAGE)
            val mod = (bookmarkList.size % BOOKMARK_COUNT_PER_PAGE)

            return if (mod == 0) {
                pageCnt * BOOKMARK_COUNT_PER_PAGE + 1
            } else {
                (pageCnt + 1) * BOOKMARK_COUNT_PER_PAGE + 1
            }
        }

    override fun onResume() {
        super.onResume()

        isVisibleProgress.set(true)

        registerDisposable(getFavoriteBookmarksUsecase.get(0).subscribeAsync({
            bookmarkList.clear()
            if (it.isNotEmpty()) {
                bookmarkList.addAll(it)
            } else {
                isVisibleEmpty.set(true)
            }
        }, {
            rxBus.send(FailToConnectionEvent())
        }, {
            isVisibleProgress.set(false)
        }))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigator.navigateToBookmark(bookmarkList[position])
    }

    @Suppress("UNUSED_PARAMETER")
    fun onScroll(listView: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

        if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {

            if (isLoading) return

            isLoading = true

            registerDisposable(getFavoriteBookmarksUsecase.get(nextIndex).subscribeAsync({
                if (it.isNotEmpty()) {
                    // TODO: 重複チェックがいるかも
                    bookmarkList.addAll(it)
                } else {
                    hasListViewFooter.set(false)
                }
            }, {
                rxBus.send(FailToConnectionEvent())
            }, {
                isLoading = false
            }))
        }
    }

    fun onRefresh() {
        if (isRefreshing.get() || isLoading) {
            return
        }

        isRefreshing.set(true)
        isLoading = true

        registerDisposable(getFavoriteBookmarksUsecase.get(0).subscribeAsync({
            bookmarkList.clear()
            if (it.isNotEmpty()) {
                bookmarkList.addAll(it)
            } else {
                isVisibleEmpty.set(true)
            }
        }, {
            rxBus.send(FailToConnectionEvent())
        }, {
            isRefreshing.set(false)
            isLoading = false
        }))
    }
}
