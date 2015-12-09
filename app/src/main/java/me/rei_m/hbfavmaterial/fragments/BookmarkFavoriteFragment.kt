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
import me.rei_m.hbfavmaterial.events.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavmaterial.events.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.BookmarkFavoriteModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.events.BookmarkFavoriteLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkFavoriteFragment : Fragment() {

    private var mUserId: String = ""

    private var mListAdapter: BookmarkListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        /**
         * ファクトリメソッド
         */
        fun newInstance(): BookmarkFavoriteFragment {
            val userModel = ModelLocator.get(ModelTag.USER) as UserModel
            val args = Bundle()
            args.putString(ARG_USER_ID, userModel.userEntity?.id)
            val fragment = BookmarkFavoriteFragment()
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

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
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
            // List内のアイテムをクリックしたらEvent発火
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(BookmarkListItemClickedEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        val emptyView = view.findViewById(R.id.fragment_list_view_empty) as TextView
        emptyView.text = getString(R.string.message_text_empty_favorite)
        listView.emptyView = emptyView

        return view
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout

        val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel

        val displayedCount = mListAdapter?.count!!

        if (displayedCount != bookmarkFavoriteModel.bookmarkList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            mListAdapter?.clear()
            mListAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
            mListAdapter?.notifyDataSetChanged()
            view.findViewById(R.id.fragment_list_progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければブックマーク情報をRSSから取得する
            bookmarkFavoriteModel.fetch(mUserId)
            view.findViewById(R.id.fragment_list_view_empty).hide()
            view.findViewById(R.id.fragment_list_progress_list).show()
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
        }

        // Pull to refreshのイベントをセット
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription!!.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            // 上から引っ張りきったらbookmarkの更新を行う
            bookmarkFavoriteModel.fetch(mUserId)
        }))
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)

        // subscription解除
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
    public fun onBookmarkFavoriteLoaded(event: BookmarkFavoriteLoadedEvent) {

        when (event.type) {
            BookmarkFavoriteLoadedEvent.Companion.Type.COMPLETE -> {
                // 正常に完了した場合、リストに追加して表示を更新
                val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()

                // フッターViewが追加されていなかった場合は追加する
                val listView = view.findViewById(R.id.fragment_list_list) as ListView
                if (listView.footerViewsCount === 0 && 0 < mListAdapter?.count!!) {
                    val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
                    listView.addFooterView(footerView, null, false)
                }
            }
            BookmarkFavoriteLoadedEvent.Companion.Type.ERROR -> {
                // 読み込み出来なかった場合はSnackbarで通知する
                val thisActivity = activity as AppCompatActivity
                thisActivity.showSnackbarNetworkError(view)
            }
        }

        // EmptyViewを再表示する（リストがからの場合のみ表示）
        view.findViewById(R.id.fragment_list_view_empty).show()

        // プログレスを非表示にする
        view.findViewById(R.id.fragment_list_progress_list).hide()

        // Pull to refresh中だった場合は解除する
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }
}
