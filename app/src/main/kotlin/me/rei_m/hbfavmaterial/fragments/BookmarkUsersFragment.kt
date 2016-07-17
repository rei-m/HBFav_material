package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.network.UserRegisterBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.ui.BookmarkUsersFilteredEvent
import me.rei_m.hbfavmaterial.events.ui.UserListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.UserRegisterBookmarkModel
import me.rei_m.hbfavmaterial.views.adapters.UserListAdapter
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * 対象の記事をブックマークしているユーザの一覧を表示するFragment.
 */
class BookmarkUsersFragment : BaseFragment() {

    @Inject
    lateinit var userRegisterBookmarkModel: UserRegisterBookmarkModel

    private val mBookmarkEntity: BookmarkEntity by lazy {
        arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    private val mListAdapter: UserListAdapter by lazy {
        UserListAdapter(activity, R.layout.list_item_user)
    }

    private var mFilterCommentFilter: BookmarkCommentFilter = BookmarkCommentFilter.ALL

    lateinit private var mCompositeSubscription: CompositeSubscription

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        private val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(bookmarkEntity: BookmarkEntity): BookmarkUsersFragment {
            return BookmarkUsersFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOKMARK, bookmarkEntity)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        if (savedInstanceState != null) {
            mFilterCommentFilter = savedInstanceState.getSerializable(KEY_FILTER_TYPE) as BookmarkCommentFilter
        } else {
            mFilterCommentFilter = BookmarkCommentFilter.ALL
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(UserListItemClickedEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            setColorSchemeResources(R.color.pull_to_refresh_1,
                    R.color.pull_to_refresh_2,
                    R.color.pull_to_refresh_3)
        }


        with(view.findViewById(R.id.fragment_list_view_empty) as TextView) {
            text = getString(R.string.message_text_empty_user)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        val view = view ?: return

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        val bookmarkUrl = mBookmarkEntity.articleEntity.url

        // Model内のURLと表示対象のURLが同じかチェックする
        if (userRegisterBookmarkModel.isSameUrl(bookmarkUrl)) {

            // 同じ場合は再表示する
            val displayedCount = mListAdapter.count

            if (displayedCount != userRegisterBookmarkModel.bookmarkList.size) {
                displayListContents(listView)
                view.findViewById(R.id.fragment_list_progress_list).hide()
            } else if (displayedCount === 0) {
                userRegisterBookmarkModel.fetch(bookmarkUrl)
                view.findViewById(R.id.fragment_list_progress_list).show()
            }
        } else {

            // Model内の情報と表示対象のURLが異なる場合は
            // 異なる記事のブックマークユーザーを表示するので1件目から再取得する
            userRegisterBookmarkModel.fetch(bookmarkUrl)
            view.findViewById(R.id.fragment_list_progress_list).show()
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            userRegisterBookmarkModel.fetch(mBookmarkEntity.articleEntity.url)
        })
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription.unsubscribe()

        val view = view ?: return

        // Pull to Refresh中であれば解除する
        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, mFilterCommentFilter)
    }

    /**
     * ブックマークユーザ情報のロード完了イベント
     */
    @Subscribe
    fun subscribe(event: UserRegisterBookmarkLoadedEvent) {

        val view = view ?: return

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        when (event.status) {
            LoadedEventStatus.OK -> {
                displayListContents(listView)
            }
            LoadedEventStatus.ERROR -> {
                // 読み込み出来なかった場合はSnackbarで通知する
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
            else -> {

            }
        }

        // リストが空の場合はEmptyViewを表示する
        view.findViewById(R.id.fragment_list_view_empty).toggle(mListAdapter.isEmpty)

        // プログレスを非表示にする
        view.findViewById(R.id.fragment_list_progress_list).hide()

        // Pull to refresh中だった場合は解除する
        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    @Subscribe
    fun subscribe(event: BookmarkUsersFilteredEvent) {
        mFilterCommentFilter = event.filterCommentFilter

        val view = view ?: return

        displayListContents(view.findViewById(R.id.fragment_list_list) as ListView)
    }

    private fun displayListContents(listView: ListView) {
        with(mListAdapter) {
            clear()
            if (mFilterCommentFilter == BookmarkCommentFilter.COMMENT) {
                addAll(userRegisterBookmarkModel.bookmarkList.filter { bookmark -> bookmark.description.isNotEmpty() })
            } else {
                addAll(userRegisterBookmarkModel.bookmarkList)
            }
            notifyDataSetChanged()
        }
        listView.setSelection(0)
    }
}
