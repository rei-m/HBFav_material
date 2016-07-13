package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.BookmarkFavoriteModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.service.BookmarkService
import me.rei_m.hbfavmaterial.service.impl.BookmarkServiceImpl
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class BookmarkFavoriteFragment : Fragment() {

    private lateinit var bookmarkService: BookmarkService

    private lateinit var bookmarkList: MutableList<BookmarkEntity>

    private var isLoading = false

    @Inject
    lateinit var userModel: UserModel

    private val listAdapter: BookmarkListAdapter by lazy {
        BookmarkListAdapter(activity, R.layout.list_item_bookmark, BookmarkFavoriteModel.BOOKMARK_COUNT_PER_PAGE)
    }

    lateinit private var subscription: CompositeSubscription

    companion object {
        fun newInstance(): BookmarkFavoriteFragment = BookmarkFavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)
        bookmarkService = BookmarkServiceImpl()
        bookmarkList = ArrayList()
        isLoading = false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        with(View.inflate(context, R.layout.list_fotter_loading, null)) {
            listView.addFooterView(this, null, false)
            hide()
        }

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    // 読込中以外、かつFooterViewが設定されている場合 = 次の読み込み対象が存在する場合、次ページ分をFetch.
                    if (!isLoading && 0 < listView.footerViewsCount) {
                        fetchAndDisplayListContents(listAdapter.nextIndex)
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

        listView.adapter = listAdapter

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            setColorSchemeResources(R.color.pull_to_refresh_1,
                    R.color.pull_to_refresh_2,
                    R.color.pull_to_refresh_3)
        }

        with(view.findViewById(R.id.fragment_list_view_empty) as TextView) {
            text = getString(R.string.message_text_empty_favorite)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        subscription = CompositeSubscription()

        val view = view ?: return

        val displayedCount = listAdapter.count

        if (displayedCount != bookmarkList.size) {
            // 表示済の件数と保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            displayListContents(view.findViewById(R.id.fragment_list_list) as ListView)
            view.findViewById(R.id.fragment_list_progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければブックマーク情報をRSSから取得する
            fetchAndDisplayListContents()
            view.findViewById(R.id.fragment_list_progress_list).show()
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        subscription.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            // 上から引っ張りきったらbookmarkの更新を行う
            fetchAndDisplayListContents()
        })
    }

    override fun onPause() {
        super.onPause()
        subscription.unsubscribe()

        val view = view ?: return

        // Pull to Refresh中であれば解除する
        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    private fun fetchAndDisplayListContents(nextIndex: Int = 0) {

        val userId = userModel.userEntity?.id ?: return

        isLoading = true

        val observer = object : Observer<List<BookmarkEntity>> {
            override fun onNext(t: List<BookmarkEntity>?) {
                t ?: return

                val view = this@BookmarkFavoriteFragment.view ?: return

                val listView = view.findViewById(R.id.fragment_list_list) as ListView

                if (t.isEmpty()) {
                    // 読込結果がなかった場合はFooterViewを非表示にする
                    if (0 < listView.footerViewsCount) {
                        with(view.findViewById(R.id.list_footer_loading_layout)) {
                            listView.removeFooterView(this)
                        }
                    }
                    return
                }

                if (nextIndex === 0) {
                    bookmarkList.clear()
                }
                bookmarkList.addAll(t)

                displayListContents(listView)
            }

            override fun onCompleted() {

                val view = this@BookmarkFavoriteFragment.view ?: return

                // リストが空の場合はEmptyViewを表示する
                view.findViewById(R.id.fragment_list_view_empty).toggle(listAdapter.isEmpty)

                // プログレスを非表示にする
                view.findViewById(R.id.fragment_list_progress_list).hide()

                // Pull to refresh中だった場合は解除する
                with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
                    if (isRefreshing) {
                        RxSwipeRefreshLayout.refreshing(this).call(false)
                    }
                }
            }

            override fun onError(e: Throwable?) {
                // 読み込み出来なかった場合はSnackbarで通知する
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
        }

        subscription.add(bookmarkService.findByUserIdForFavorite(userId, nextIndex)
                .doOnCompleted { isLoading = false }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer))
    }

    private fun displayListContents(listView: ListView) {

        with(listAdapter) {
            clear()
            addAll(bookmarkList)
            notifyDataSetChanged()
        }

        if (0 < listView.footerViewsCount) {
            listView.findViewById(R.id.list_footer_loading_layout).show()
        }
    }
}
