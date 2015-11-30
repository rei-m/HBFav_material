package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.events.BookmarkUserLoadedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.BookmarkUserModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.events.BookmarkUserLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkUserFragment : Fragment() {

    private var mUserId: String = ""
    private var mIsOwner: Boolean = true

    private var mListAdapter: BookmarkListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"
        private val ARG_OWNER_FLAG = "ARG_OWNER_FLAG"

        fun newInstance(): BookmarkUserFragment {

            val userModel = ModelLocator.get(ModelTag.USER) as UserModel
            val args = Bundle()
            args.putString(ARG_USER_ID, userModel.userEntity?.id)
            args.putBoolean(ARG_OWNER_FLAG, true)

            val fragment = BookmarkUserFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(userId: String): BookmarkUserFragment {
            val fragment = BookmarkUserFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userId)
            args.putBoolean(ARG_OWNER_FLAG, false)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = BookmarkListAdapter(activity, R.layout.list_item_bookmark)
        mUserId = arguments.getString(ARG_USER_ID)
        mIsOwner = arguments.getBoolean(ARG_OWNER_FLAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        mListAdapter = null
        mCompositeSubscription = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.list) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val favoriteModel = getBookmarkModel()
                    if (!favoriteModel.isBusy) {
                        favoriteModel.fetch(mUserId, mListAdapter!!.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(BookmarkListItemClickedEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout

        val bookmarkUserModel = getBookmarkModel()

        if (bookmarkUserModel.isSameUser(mUserId)) {
            val displayedCount = mListAdapter?.count!!

            if (displayedCount != bookmarkUserModel.bookmarkList.size) {
                // 表示済の件数とModel内で保持している件数をチェックし、
                // 差分があれば未表示のブックマークがあるのでリストに表示する
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkUserModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()
                view.findViewById(R.id.progress_list).hide()
            } else if (displayedCount === 0) {
                // 1件も表示していなければお気に入りのブックマーク情報を取得する
                bookmarkUserModel.fetch(mUserId)
                view.findViewById(R.id.progress_list).show()
                RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
            }
        } else {
            // Model内の情報と他人のブックマークを表示する場合は1件目から再取得
            bookmarkUserModel.fetch(mUserId)
            view.findViewById(R.id.progress_list).show()
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
        }

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            bookmarkUserModel.fetch(mUserId)
        }))
    }

    override fun onPause() {
        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription?.unsubscribe()

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }

        super.onPause()
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkOwnLoaded(event: BookmarkUserLoadedEvent) {

        when (event.type) {
            BookmarkUserLoadedEvent.Companion.Type.COMPLETE -> {

                val bookmarkUserModel = getBookmarkModel()
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkUserModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()

                val listView = view.findViewById(R.id.list) as ListView
                if (listView.footerViewsCount === 0) {
                    val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
                    listView.addFooterView(footerView, null, false)
                }
            }
            BookmarkUserLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }

        view.findViewById(R.id.progress_list).hide()

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    private fun getBookmarkModel(): BookmarkUserModel {
        return if (mIsOwner) {
            ModelLocator.get(ModelTag.OWN_BOOKMARK) as BookmarkUserModel
        } else {
            ModelLocator.get(ModelTag.OTHERS_BOOKMARK) as BookmarkUserModel
        }
    }
}
