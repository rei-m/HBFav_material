package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
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
import me.rei_m.hbfavmaterial.events.BookmarkUsersFilteredEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserListItemClickedEvent
import me.rei_m.hbfavmaterial.events.UserRegisterBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.UserRegisterBookmarkModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.FilterType
import me.rei_m.hbfavmaterial.views.adapters.UserListAdapter
import rx.Observable
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkUsersFragment : Fragment() {

    private var mBookmarkEntity: BookmarkEntity? = null

    private var mFilterType: FilterType? = null

    private var mListAdapter: UserListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"
        private val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(bookmarkEntity: BookmarkEntity): BookmarkUsersFragment {
            val fragment = BookmarkUsersFragment()
            val args = Bundle()
            args.putSerializable(ARG_BOOKMARK, bookmarkEntity)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = UserListAdapter(activity, R.layout.list_item_user)
        mBookmarkEntity = arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity

        if (savedInstanceState != null) {
            mFilterType = savedInstanceState.getSerializable(KEY_FILTER_TYPE) as FilterType
        } else {
            mFilterType = FilterType.ALL
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mListAdapter = null
        mBookmarkEntity = null
        mFilterType = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(UserListItemClickedEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        val emptyView = view.findViewById(R.id.fragment_list_view_empty) as TextView
        emptyView.text = getString(R.string.message_text_empty_user)

        return view
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.pull_to_refresh_1, R.color.pull_to_refresh_2, R.color.pull_to_refresh_3)

        val userRegisterBookmarkModel = ModelLocator.get(ModelTag.USER_REGISTER_BOOKMARK) as UserRegisterBookmarkModel

        if (userRegisterBookmarkModel.isSameUrl(mBookmarkEntity?.link!!)) {
            val displayedCount = mListAdapter?.count!!
            if (displayedCount != userRegisterBookmarkModel.bookmarkList.size) {
                displayBookmarkUsers()
                view.findViewById(R.id.fragment_list_progress_list).hide()
            } else if (displayedCount === 0) {
                userRegisterBookmarkModel.fetch(mBookmarkEntity?.link!!)
                view.findViewById(R.id.fragment_list_progress_list).show()
                RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
            }
        } else {
            userRegisterBookmarkModel.fetch(mBookmarkEntity?.link!!)
            view.findViewById(R.id.fragment_list_progress_list).show()
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription!!.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            userRegisterBookmarkModel.fetch(mBookmarkEntity?.link!!)
        }))
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription?.unsubscribe()

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, mFilterType)
    }

    @Subscribe
    public fun onUserRegisterBookmarkLoaded(event: UserRegisterBookmarkLoadedEvent) {

        when (event.type) {
            UserRegisterBookmarkLoadedEvent.Companion.Type.COMPLETE -> {
                displayBookmarkUsers()
            }
            UserRegisterBookmarkLoadedEvent.Companion.Type.ERROR -> {
                // 読み込み出来なかった場合はSnackbarで通知する
                val thisActivity = activity as AppCompatActivity
                thisActivity.showSnackbarNetworkError(view)
            }
        }

        // リストが空の場合はEmptyViewを表示する
        if (mListAdapter?.isEmpty!!) {
            view.findViewById(R.id.fragment_list_view_empty).show()
        }

        view.findViewById(R.id.fragment_list_progress_list).hide()

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    @Subscribe
    public fun onBookmarkUsersFiltered(event: BookmarkUsersFilteredEvent) {
        mFilterType = event.filterType
        displayBookmarkUsers()
    }

    private fun displayBookmarkUsers() {
        val userRegisterBookmarkModel = ModelLocator.get(ModelTag.USER_REGISTER_BOOKMARK) as UserRegisterBookmarkModel
        mListAdapter?.clear()
        if (mFilterType == FilterType.COMMENT) {
            Observable.from(userRegisterBookmarkModel.bookmarkList).filter({ bookmark ->
                bookmark.description != ""
            }).subscribe({ bookmark ->
                mListAdapter?.add(bookmark)
            })
        } else {
            mListAdapter?.addAll(userRegisterBookmarkModel.bookmarkList)
        }
        mListAdapter?.notifyDataSetChanged()
    }

}
