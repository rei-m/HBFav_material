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
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.BookmarkUserModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.events.BookmarkUserLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkUserFragment : Fragment() {

    private var mUserId: String = ""

    private var mListAdapter: BookmarkListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    private var mIsOwner: Boolean = true

    companion object {

        private val ARG_USER_ID = "ARG_USER_ID"

        private val ARG_OWNER_FLAG = "ARG_OWNER_FLAG"

        /**
         * ファクトリメソッド
         *
         * 自分のブックマークを表示する
         */
        fun newInstance(): BookmarkUserFragment {
            val userModel = ModelLocator.get(ModelTag.USER) as UserModel
            val args = Bundle()
            args.putString(ARG_USER_ID, userModel.userEntity?.id)
            args.putBoolean(ARG_OWNER_FLAG, true)
            val fragment = BookmarkUserFragment()
            fragment.arguments = args
            return fragment
        }

        /**
         * ファクトリメソッド
         *
         * 他人のブックマークを表示する
         */
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

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
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
            // List内のアイテムをクリックしたらEvent発火
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(BookmarkListItemClickedEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        val emptyView = view.findViewById(R.id.fragment_list_view_empty) as TextView
        emptyView.text = getString(R.string.message_text_empty_bookmark_user)

        return view
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.pull_to_refresh_1, R.color.pull_to_refresh_2, R.color.pull_to_refresh_3)

        val bookmarkUserModel = getBookmarkModel()

        // Model内のユーザーIDと表示対象のユーザーIDが同じかチェックする
        if (bookmarkUserModel.isSameUser(mUserId)) {

            // 同じ場合は再表示する
            val displayedCount = mListAdapter?.count!!

            if (displayedCount != bookmarkUserModel.bookmarkList.size) {
                // 表示済の件数とModel内で保持している件数をチェックし、
                // 差分があれば未表示のブックマークがあるのでリストに表示する
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkUserModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()
                view.findViewById(R.id.fragment_list_progress_list).hide()
            } else if (displayedCount === 0) {
                // 1件も表示していなければブックマーク情報をRSSから取得する
                bookmarkUserModel.fetch(mUserId)
                view.findViewById(R.id.fragment_list_progress_list).show()
                RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
            }
        } else {
            // Model内の情報と表示対象のユーザーIDが異なる場合は
            // 他人のブックマークを表示するので1件目から再取得する
            bookmarkUserModel.fetch(mUserId)
            view.findViewById(R.id.fragment_list_progress_list).show()
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(true)
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        // Pull to refreshのイベントをセット
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription!!.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            // 上から引っ張りきったらbookmarkの更新を行う
            bookmarkUserModel.fetch(mUserId)
        }))
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription?.unsubscribe()

        // Pull to Refresh中であれば解除する
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    @Subscribe
    public fun onBookmarkOwnLoaded(event: BookmarkUserLoadedEvent) {

        when (event.type) {
            BookmarkUserLoadedEvent.Companion.Type.COMPLETE -> {
                // 正常に完了した場合、リストに追加して表示を更新
                val bookmarkUserModel = getBookmarkModel()
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkUserModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()

                // フッターViewが追加されていなかった場合は追加する
                val listView = view.findViewById(R.id.fragment_list_list) as ListView
                if (listView.footerViewsCount === 0) {
                    val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
                    listView.addFooterView(footerView, null, false)
                }
            }
            BookmarkUserLoadedEvent.Companion.Type.ERROR -> {
                // 読み込み出来なかった場合はSnackbarで通知する
                val thisActivity = activity as AppCompatActivity
                thisActivity.showSnackbarNetworkError(view)
            }
        }

        // リストが空の場合はEmptyViewを表示する
        if (mListAdapter?.isEmpty!!) {
            view.findViewById(R.id.fragment_list_view_empty).show()
        }

        // プログレスを非表示にする
        view.findViewById(R.id.fragment_list_progress_list).hide()

        // Pull to refresh中だった場合は解除する
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }

    /**
     * 表示に使用するブックマークモデルを取得する
     */
    private fun getBookmarkModel(): BookmarkUserModel {
        return if (mIsOwner) {
            ModelLocator.get(ModelTag.OWN_BOOKMARK) as BookmarkUserModel
        } else {
            ModelLocator.get(ModelTag.OTHERS_BOOKMARK) as BookmarkUserModel
        }
    }
}
