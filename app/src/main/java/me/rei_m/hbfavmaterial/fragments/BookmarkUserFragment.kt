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
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.events.BookmarkUserLoadedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.LoadedEventStatus
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.BookmarkUserModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

/**
 * 特定のユーザーのブックマークを一覧で表示するFragment.
 */
public class BookmarkUserFragment : Fragment() {

    private var mUserId: String = ""

    private var mListAdapter: BookmarkListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    private var mIsOwner: Boolean = true

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        private val ARG_OWNER_FLAG = "ARG_OWNER_FLAG"

        /**
         * 自分のブックマークを表示する
         *
         * @return Fragment
         */
        fun newInstance(): BookmarkUserFragment {
            return BookmarkUserFragment().apply {
                arguments = Bundle().apply {
                    val userModel = ModelLocator.get(ModelTag.USER) as UserModel
                    putString(ARG_USER_ID, userModel.userEntity?.id)
                    putBoolean(ARG_OWNER_FLAG, true)
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
                }
            }
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

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val bookmarkUserModel = getBookmarkModel()
                    // 読込中以外、かつFooterViewが設定されている場合 = 次の読み込み対象が存在する場合、次ページ分をFetch.
                    if (!bookmarkUserModel.isBusy && 0 < listView.footerViewsCount) {
                        bookmarkUserModel.fetch(mUserId, mListAdapter?.nextIndex!!)
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

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.pull_to_refresh_1, R.color.pull_to_refresh_2, R.color.pull_to_refresh_3)

        val emptyView = view.findViewById(R.id.fragment_list_view_empty) as TextView
        emptyView.text = getString(R.string.message_text_empty_bookmark_user)

        return view
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        val bookmarkUserModel = getBookmarkModel()

        // Model内のユーザーIDと表示対象のユーザーIDが同じかチェックする
        if (bookmarkUserModel.isSameUser(mUserId)) {

            // 同じ場合は再表示する
            val displayedCount = mListAdapter?.count!!

            if (displayedCount != bookmarkUserModel.bookmarkList.size) {
                // 表示済の件数とModel内で保持している件数をチェックし、
                // 差分があれば未表示のブックマークがあるのでリストに表示する
                displayListContents()
                view.findViewById(R.id.fragment_list_progress_list).hide()
            } else if (displayedCount === 0) {
                // 1件も表示していなければブックマーク情報をRSSから取得する
                bookmarkUserModel.fetch(mUserId)
                view.findViewById(R.id.fragment_list_progress_list).show()
            }
        } else {

            // Model内の情報と表示対象のユーザーIDが異なる場合は
            // 他人のブックマークを表示するので1件目から再取得する
            bookmarkUserModel.fetch(mUserId)
            view.findViewById(R.id.fragment_list_progress_list).show()
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription!!.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            // 上から引っ張りきったらbookmarkの更新を行う
            bookmarkUserModel.fetch(mUserId)
        })
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription?.unsubscribe()

        // Pull to Refresh中であれば解除する
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    /**
     * ブックマーク情報のロード完了イベント
     */
    @Subscribe
    public fun subscribe(event: BookmarkUserLoadedEvent) {

        when (event.status) {
            LoadedEventStatus.OK -> {
                // 正常に完了した場合、リストに追加して表示を更新
                displayListContents()
            }
            LoadedEventStatus.NOT_FOUND -> {
                // 読込結果がなかった場合はFooterViewを非表示にする
                val listView = view.findViewById(R.id.fragment_list_list) as ListView

                if (0 < listView.footerViewsCount) {
                    val footerView = view.findViewById(R.id.list_footer_loading_layout)
                    listView.removeFooterView(footerView)
                }
            }
            LoadedEventStatus.ERROR -> {
                // 読み込み出来なかった場合はSnackbarで通知する
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
            else -> {

            }
        }

        // リストが空の場合はEmptyViewを表示する
        view.findViewById(R.id.fragment_list_view_empty).toggle(mListAdapter?.isEmpty!!)

        // プログレスを非表示にする
        view.findViewById(R.id.fragment_list_progress_list).hide()

        // Pull to refresh中だった場合は解除する
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    /**
     * ListViewのコンテンツを表示する
     */
    private fun displayListContents() {

        // コンテンツを表示する
        mListAdapter?.apply {
            val bookmarkUserModel = getBookmarkModel()
            clear()
            addAll(bookmarkUserModel.bookmarkList)
            notifyDataSetChanged()
        }

        // FooterViewを表示する
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        if (listView.footerViewsCount == 0) {
            val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
            listView.addFooterView(footerView, null, false)
        }
    }

    /**
     * 表示に使用するブックマークモデルを取得する
     */
    private fun getBookmarkModel(): BookmarkUserModel {
        return ModelLocator.get(if (mIsOwner) ModelTag.OWN_BOOKMARK else ModelTag.OTHERS_BOOKMARK) as BookmarkUserModel
    }
}
