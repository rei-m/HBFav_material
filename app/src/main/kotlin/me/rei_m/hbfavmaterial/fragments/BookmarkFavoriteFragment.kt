package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
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
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.fragments.presenter.BookmarkFavoriteContact
import me.rei_m.hbfavmaterial.fragments.presenter.BookmarkFavoritePresenter
import me.rei_m.hbfavmaterial.models.BookmarkFavoriteModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class BookmarkFavoriteFragment : BaseFragment(), BookmarkFavoriteContact.View {

    private lateinit var presenter: BookmarkFavoritePresenter

    private val listAdapter: BookmarkListAdapter by lazy {
        BookmarkListAdapter(activity, R.layout.list_item_bookmark, BookmarkFavoriteModel.BOOKMARK_COUNT_PER_PAGE)
    }

    private var subscription: CompositeSubscription? = null

    companion object {
        fun newInstance(): BookmarkFavoriteFragment = BookmarkFavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = BookmarkFavoritePresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    // FooterViewが設定されている場合 = 次の読み込み対象が存在する場合、次ページ分をFetch.
                    if (0 < listView.footerViewsCount) {
                        presenter.fetchListContents(listAdapter.nextIndex)?.let {
                            subscription?.add(it)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            presenter.clickBookmark(bookmarkEntity)
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

        if (listAdapter.count === 0) {
            // 1件も表示していなければブックマーク情報をRSSから取得する
            presenter.fetchListContents(0)?.let {
                subscription?.add(it)
                view.findViewById(R.id.fragment_list_progress_list).show()
            }
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        subscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            // 上から引っ張りきったらbookmarkの更新を行う
            presenter.fetchListContents(0)?.let {
                subscription?.add(it)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
        subscription = null

        val view = view ?: return

        // Pull to Refresh中であれば解除する
        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun showBookmarkList(bookmarkList: List<BookmarkEntity>) {

        val view = view ?: return

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        with(listAdapter) {
            clear()
            addAll(bookmarkList)
            notifyDataSetChanged()
        }

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

    override fun showNetworkErrorMessage() {
        (activity as AppCompatActivity).showSnackbarNetworkError(view)
    }

    override fun startAutoLoading() {
        val view = view ?: return
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        if (listView.footerViewsCount === 0) {
            View.inflate(context, R.layout.list_fotter_loading, null).let {
                listView.addFooterView(it, null, false)
            }
        }
    }

    override fun stopAutoLoading() {
        val view = view ?: return
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        if (0 < listView.footerViewsCount) {
            with(view.findViewById(R.id.list_footer_loading_layout)) {
                listView.removeFooterView(this)
            }
        }
    }
}
