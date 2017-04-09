package me.rei_m.hbfavmaterial.viewmodel.fragment

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.ReadAfterFilter
import me.rei_m.hbfavmaterial.model.UserBookmarkModel
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory

class UserBookmarkFragmentViewModel(private val userBookmarkModel: UserBookmarkModel,
                                    private val userModel: UserModel,
                                    private val navigator: Navigator) : AbsFragmentViewModel() {

    val bookmarkList: ObservableArrayList<BookmarkEntity> = ObservableArrayList()

    val isVisibleEmpty: ObservableBoolean = ObservableBoolean(false)

    val isVisibleProgress: ObservableBoolean = ObservableBoolean(false)

    val isRefreshing: ObservableBoolean = ObservableBoolean(false)

    private var readAllItemEventSubject = PublishSubject.create<Unit>()
    val readAllItemEvent: io.reactivex.Observable<Unit> = readAllItemEventSubject

    private var updateFilterEventSubject = PublishSubject.create<Unit>()
    val updateFilterEvent: io.reactivex.Observable<Unit> = updateFilterEventSubject

    private var snackbarFactory: SnackbarFactory? = null

    var isOwner: Boolean = false
        private set

    var bookmarkUserId: String = ""
        private set

    var readAfterFilter = ReadAfterFilter.ALL
        private set

    fun onCreate(isOwner: Boolean, bookmarkUserId: String, readAfterFilter: ReadAfterFilter) {
        this.isOwner = isOwner
        this.bookmarkUserId = bookmarkUserId
        this.readAfterFilter = readAfterFilter
    }

    fun onCreateView(snackbarFactory: SnackbarFactory) {
        this.snackbarFactory = snackbarFactory
    }

    override fun onStart() {
        super.onStart()
        registerDisposable(userBookmarkModel.bookmarkListUpdatedEvent.subscribe {
            bookmarkList.clear()
            bookmarkList.addAll(it)
            isVisibleEmpty.set(it.isEmpty())
            isVisibleProgress.set(false)
            isRefreshing.set(false)
        }, userBookmarkModel.hasNextPageUpdatedEvent.subscribe {
            if (!it) {
                readAllItemEventSubject.onNext(Unit)
            }
        }, userBookmarkModel.readAfterFilterUpdatedEvent.subscribe {
            readAfterFilter = it
            updateFilterEventSubject.onNext(Unit)
        }, userBookmarkModel.error.subscribe {
            isVisibleProgress.set(false)
            isRefreshing.set(false)
            snackbarFactory?.create(R.string.message_error_network)?.show()
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

    fun onDestroyView() {
        snackbarFactory = null
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
