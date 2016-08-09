package me.rei_m.hbfavmaterial.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.enum.ReadAfterFilter
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.fragment.presenter.BookmarkUserContact
import me.rei_m.hbfavmaterial.view.adapter.BookmarkListAdapter
import me.rei_m.hbfavmaterial.view.adapter.BookmarkPagerAdaptor
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * 特定のユーザーのブックマークを一覧で表示するFragment.
 */
class BookmarkUserFragment() : BaseFragment(),
        BookmarkUserContact.View,
        MainPageFragment {

    companion object {

        private const val BOOKMARK_COUNT_PER_PAGE = 20

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val ARG_USER_ID = "ARG_USER_ID"

        private const val ARG_OWNER_FLAG = "ARG_OWNER_FLAG"

        /**
         * 自分のブックマークを表示する
         *
         * @return Fragment
         */
        fun newInstance(pageIndex: Int): BookmarkUserFragment {
            return BookmarkUserFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_OWNER_FLAG, true)
                    putInt(ARG_PAGE_INDEX, pageIndex)
                }
            }
        }

        /**
         * 他人のブックマークを表示する
         *
         * @userId: 表示対象のユーザーのID.
         * @return Fragment
         */
        fun newInstance(userId: String): BookmarkUserFragment {
            return BookmarkUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_ID, userId)
                    putBoolean(ARG_OWNER_FLAG, false)
                    putInt(ARG_PAGE_INDEX, 0)
                }
            }
        }
    }

    @Inject
    lateinit var presenter: BookmarkUserContact.Actions

    private var listener: OnFragmentInteractionListener? = null

    private val listAdapter: BookmarkListAdapter by lazy {
        BookmarkListAdapter(activity, R.layout.list_item_bookmark, BOOKMARK_COUNT_PER_PAGE)
    }

    private var subscription: CompositeSubscription? = null

    override val pageIndex: Int
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdaptor.Page.values()[pageIndex].title(getAppContext(), presenter.readAfterFilter.title(getAppContext()))

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        val isOwner = arguments.getBoolean(ARG_OWNER_FLAG)
        val userId = arguments.getString(ARG_USER_ID) ?: ""

        require(isOwner || (!isOwner && userId.isNotEmpty())) {
            "UserId is empty !!"
        }

        presenter.onCreate(component, this, isOwner, userId)
        setHasOptionsMenu(true)
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
            text = getString(R.string.message_text_empty_bookmark_user)
        }

        // Pull to refreshのイベントをセット
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

        // Pull to Refresh中であれば解除する
        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_bookmark_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = ReadAfterFilter.forMenuId(item.itemId)

        presenter.onOptionItemSelected(filter)

        listener?.onChangeFilter(pageTitle)

        return true
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

    interface OnFragmentInteractionListener {
        fun onChangeFilter(newPageTitle: String)
    }
}
