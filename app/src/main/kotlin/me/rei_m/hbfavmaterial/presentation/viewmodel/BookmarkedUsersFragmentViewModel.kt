package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.GetBookmarkedUsersUsecase

class BookmarkedUsersFragmentViewModel(private val getBookmarkedUsersUsecase: GetBookmarkedUsersUsecase,
                                       private val rxBus: RxBus,
                                       private val navigator: ActivityNavigator) : AbsFragmentViewModel() {

    val bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var bookmark: BookmarkEntity? = null

    var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL

    private val originalBookmarkList: MutableList<BookmarkEntity> = mutableListOf()

    private var isLoading: Boolean = false

    override fun onResume() {
        super.onResume()

        val bookmark = this.bookmark ?: return

        isVisibleProgress.set(true)

        registerDisposable(getBookmarkedUsersUsecase.get(bookmark).subscribeAsync({
            originalBookmarkList.clear()
            bookmarkList.clear()
            if (it.isNotEmpty()) {
                originalBookmarkList.addAll(it)
                if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
                    bookmarkList.addAll(originalBookmarkList.filter { (_, description) -> description.isNotEmpty() })
                } else {
                    bookmarkList.addAll(originalBookmarkList)
                }
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
        navigator.navigateToOthersBookmark(bookmarkList[position].creator)
    }

    fun onRefresh() {
        val bookmark = this.bookmark ?: return

        if (isRefreshing.get() || isLoading) {
            return
        }

        isRefreshing.set(true)
        isLoading = true

        registerDisposable(getBookmarkedUsersUsecase.get(bookmark).subscribeAsync({
            originalBookmarkList.clear()
            bookmarkList.clear()
            if (it.isNotEmpty()) {
                originalBookmarkList.addAll(it)
                if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
                    bookmarkList.addAll(originalBookmarkList.filter { (_, description) -> description.isNotEmpty() })
                } else {
                    bookmarkList.addAll(originalBookmarkList)
                }
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

    fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {

        if (this.bookmarkCommentFilter == bookmarkCommentFilter) return

        this.bookmarkCommentFilter = bookmarkCommentFilter

        bookmarkList.clear()
        if (bookmarkCommentFilter == BookmarkCommentFilter.COMMENT) {
            bookmarkList.addAll(originalBookmarkList.filter { (_, description) -> description.isNotEmpty() })
        } else {
            bookmarkList.addAll(originalBookmarkList)
        }
    }
}
