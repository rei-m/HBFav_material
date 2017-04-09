package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.model.BookmarkModel
import me.rei_m.hbfavmaterial.model.entity.BookmarkUserEntity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class BookmarkedUsersFragmentViewModel(private val bookmarkModel: BookmarkModel,
                                       private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmarkUserList: ObservableArrayList<BookmarkUserEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    var articleUrl: String = ""
        private set

    var bookmarkCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL
        private set

    private var snackbarFactory: SnackbarFactory? = null

    fun onCreate(articleUrl: String, bookmarkCommentFilter: BookmarkCommentFilter) {
        this.articleUrl = articleUrl
        this.bookmarkCommentFilter = bookmarkCommentFilter
    }

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

    override fun onStart() {
        super.onStart()

        registerDisposable(bookmarkModel.userListUpdatedEvent.subscribe {
            bookmarkUserList.clear()
            bookmarkUserList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
        }, bookmarkModel.error.subscribe {
            snackbarFactory?.create(R.string.message_error_network)?.show()
        }, bookmarkModel.bookmarkCommentFilterUpdatedEvent.subscribe {
            bookmarkCommentFilter = it
        })
    }

    override fun onResume() {
        super.onResume()
        if (bookmarkUserList.isEmpty()) {
            isVisibleProgress.set(true)
            bookmarkModel.getUserList(articleUrl, bookmarkCommentFilter)
        }
    }

    fun onDestroyView() {
        snackbarFactory = null
    }

    @Suppress("UNUSED_PARAMETER")
    fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        navigator.navigateToOthersBookmark(bookmarkUserList[position].creator)
    }

    fun onRefresh() {
        isRefreshing.set(true)
        bookmarkModel.getUserList(articleUrl, bookmarkCommentFilter)
    }

    fun onOptionItemSelected(bookmarkCommentFilter: BookmarkCommentFilter) {
        if (this.bookmarkCommentFilter == bookmarkCommentFilter) return
        bookmarkModel.getUserList(articleUrl, bookmarkCommentFilter)
    }
}
