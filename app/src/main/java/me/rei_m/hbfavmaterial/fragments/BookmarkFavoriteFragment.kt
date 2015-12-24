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
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.ui.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.BookmarkFavoriteModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

/**
 * お気に入りのブックマークを一覧で表示するFragment.
 */
public class BookmarkFavoriteFragment : Fragment() {

    private var mUserId: String = ""

    private var mListAdapter: BookmarkListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        fun newInstance(): BookmarkFavoriteFragment {
            return BookmarkFavoriteFragment().apply {
                arguments = Bundle().apply {
                    val userModel = ModelLocator.get(ModelTag.USER) as UserModel
                    putString(ARG_USER_ID, userModel.userEntity?.id)
                }
            }
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

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
        listView.addFooterView(footerView, null, false)
        footerView.hide()

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val favoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                    // 読込中以外、かつFooterViewが設定されている場合 = 次の読み込み対象が存在する場合、次ページ分をFetch.
                    if (!favoriteModel.isBusy && 0 < listView.footerViewsCount) {
                        favoriteModel.fetch(mUserId, mListAdapter?.nextIndex ?: 0)
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
        emptyView.text = getString(R.string.message_text_empty_favorite)

        return view
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel

        val displayedCount = mListAdapter?.count

        if (displayedCount != bookmarkFavoriteModel.bookmarkList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            displayListContents()
            view.findViewById(R.id.fragment_list_progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければブックマーク情報をRSSから取得する
            bookmarkFavoriteModel.fetch(mUserId)
            view.findViewById(R.id.fragment_list_progress_list).show()
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription!!.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            // 上から引っ張りきったらbookmarkの更新を行う
            bookmarkFavoriteModel.fetch(mUserId)
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
    public fun subscribe(event: BookmarkFavoriteLoadedEvent) {

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
        view.findViewById(R.id.fragment_list_view_empty).toggle(mListAdapter?.isEmpty ?: true)

        // プログレスを非表示にする
        view.findViewById(R.id.fragment_list_progress_list).hide()

        // Pull to refresh中だった場合は解除する
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    /**
     * ListViewのコンテンツを表示する.
     */
    private fun displayListContents() {

        // コンテンツを表示する
        mListAdapter?.apply {
            val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
            clear()
            addAll(bookmarkFavoriteModel.bookmarkList)
            notifyDataSetChanged()
        }

        // FooterViewを表示する
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        if (0 < listView.footerViewsCount) {
            view.findViewById(R.id.list_footer_loading_layout).show()
        }
    }
}
