package me.rei_m.hbfavkotlin.fragments

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
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavkotlin.events.BookmarkListItemClickedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.extensions.hide
import me.rei_m.hbfavkotlin.extensions.show
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.BookmarkFavoriteModel
import me.rei_m.hbfavkotlin.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkFavoriteFragment : Fragment() {

    private var mUserId: String = ""

    private var mListAdapter: BookmarkListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        fun newInstance(): BookmarkFavoriteFragment {
            val fragment = BookmarkFavoriteFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, "Rei19")
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = BookmarkListAdapter(activity, R.layout.list_item_bookmark)
        mUserId = arguments.getString(ARG_USER_ID)
    }

    override fun onDestroy() {
        super.onDestroy()
        mListAdapter = null
        mCompositeSubscription = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_bookmark_list, container, false)

        val listView = view.findViewById(R.id.list_bookmark) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val favoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                    if (!favoriteModel.isBusy) {
                        favoriteModel.fetch(mUserId, mListAdapter?.nextIndex!!)
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

        val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel

        val displayedCount = mListAdapter?.count!!

        if (displayedCount != bookmarkFavoriteModel.bookmarkList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            mListAdapter?.clear()
            mListAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
            mListAdapter?.notifyDataSetChanged()
            view.findViewById(R.id.progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのブックマーク情報を取得する
            bookmarkFavoriteModel.fetch(mUserId)
            view.findViewById(R.id.progress_list).show()
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
        }

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            bookmarkFavoriteModel.fetch(mUserId)
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
    public fun onBookmarkFavoriteLoaded(event: BookmarkFavoriteLoadedEvent) {

        when (event.type) {
            BookmarkFavoriteLoadedEvent.Companion.Type.COMPLETE -> {

                val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()

                val listView = view.findViewById(R.id.list_bookmark) as ListView
                if (listView.footerViewsCount === 0) {
                    val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
                    listView.addFooterView(footerView, null, false)
                }
            }
            BookmarkFavoriteLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }

        view.findViewById(R.id.progress_list).hide()

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }
}
