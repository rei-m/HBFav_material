package me.rei_m.hbfavmaterial.fragment

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
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.fragment.presenter.BookmarkFavoriteContact
import me.rei_m.hbfavmaterial.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.view.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.view.adapter.BookmarkPagerAdaptor
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
class BookmarkFavoriteFragment() : BaseFragment(),
        BookmarkFavoriteContact.View,
        MainPageFragment {

    companion object {

        private const val BOOKMARK_COUNT_PER_PAGE = 25

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        fun newInstance(pageIndex: Int): BookmarkFavoriteFragment {
            return BookmarkFavoriteFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PAGE_INDEX, pageIndex)
                }
            }
        }
    }

    @Inject
    lateinit var presenter: BookmarkFavoriteContact.Actions

    @Inject
    lateinit var activityNavigator: ActivityNavigator

    private val listAdapter: BookmarkListAdapter by lazy {
        BookmarkListAdapter(activity, R.layout.list_item_bookmark, BOOKMARK_COUNT_PER_PAGE)
    }

    private var subscription: CompositeSubscription? = null

    override val pageIndex: Int
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdaptor.Page.values()[pageIndex].title(getAppContext(), "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        presenter.onCreate(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        subscription = CompositeSubscription()

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    // FooterViewが設定されている場合 = 次の読み込み対象が存在する場合、次ページ分をFetch.
                    if (0 < listView.footerViewsCount) {
                        presenter.onScrollEnd(listAdapter.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            presenter.onClickBookmark(bookmarkEntity)
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

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        subscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            presenter.onRefreshList()
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        subscription?.unsubscribe()
        subscription = null

        val view = view ?: return

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun showBookmarkList(bookmarkList: List<BookmarkEntity>) {

        val view = view ?: return

        with(listAdapter) {
            clear()
            addAll(bookmarkList)
            notifyDataSetChanged()
        }

        view.findViewById(R.id.fragment_list_list).show()

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun hideBookmarkList() {
        val view = view ?: return
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        listView.setSelection(0)
        listView.hide()
    }

    override fun showNetworkErrorMessage() {
        (activity as AppCompatActivity).showSnackbarNetworkError(view)
    }

    override fun showProgress() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_progress_list).show()
    }

    override fun hideProgress() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_progress_list).hide()
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

    override fun showEmpty() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_view_empty).show()
    }

    override fun hideEmpty() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_view_empty).hide()
    }

    override fun navigateToBookmark(bookmarkEntity: BookmarkEntity) {
        activityNavigator.navigateToBookmark(activity, bookmarkEntity)
    }
}
