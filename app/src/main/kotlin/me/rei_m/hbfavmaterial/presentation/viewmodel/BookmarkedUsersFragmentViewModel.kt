package me.rei_m.hbfavmaterial.presentation.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.domain.entity.BookmarkUserEntity
import me.rei_m.hbfavmaterial.domain.model.BookmarkModel
import me.rei_m.hbfavmaterial.presentation.event.FailToConnectionEvent
import me.rei_m.hbfavmaterial.presentation.event.RxBus
import me.rei_m.hbfavmaterial.presentation.helper.Navigator

class BookmarkedUsersFragmentViewModel(private val bookmarkModel: BookmarkModel,
                                       private val rxBus: RxBus,
                                       private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmarkUserList: ObservableArrayList<BookmarkUserEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var articleUrl: String = ""
        private set

    var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL
        private set

    fun onCreate(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter) {
        this.articleUrl = articleUrl
        this.bookmarkCommentFilter = bookmarkCommentFilter
    }

    override fun onStart() {
        super.onStart()

        bookmarkModel.articleUrl = articleUrl

        registerDisposable(bookmarkModel.userList.subscribe {
            bookmarkUserList.clear()
            bookmarkUserList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
        }, bookmarkModel.error.subscribe {
            rxBus.send(FailToConnectionEvent())
        }, bookmarkModel.bookmarkCommentFilter.subscribe {
            bookmarkCommentFilter = it
        })
    }

    override fun onResume() {
        super.onResume()
        if (bookmarkUserList.isEmpty()) {
            isVisibleProgress.set(true)
            bookmarkModel.getUserList(bookmarkCommentFilter)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigator.navigateToOthersBookmark(bookmarkUserList[position].creator)
    }

    fun onRefresh() {
        isRefreshing.set(true)
        bookmarkModel.getUserList(bookmarkCommentFilter)
    }

    fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {
        if (this.bookmarkCommentFilter == bookmarkCommentFilter) return
        bookmarkModel.getUserList(bookmarkCommentFilter)
    }
}
