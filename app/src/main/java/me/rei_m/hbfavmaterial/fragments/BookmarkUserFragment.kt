package me.rei_m.hbfavmaterial.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentListBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkUserLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.ui.BookmarkListItemClickedEvent
import me.rei_m.hbfavmaterial.events.ui.ReadAfterFilterChangedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.BookmarkUserModel
import me.rei_m.hbfavmaterial.models.UserModel
import me.rei_m.hbfavmaterial.views.adapters.BookmarkListAdapter
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject
import javax.inject.Named

/**
 * 特定のユーザーのブックマークを一覧で表示するFragment.
 */
class BookmarkUserFragment : Fragment() {

    @field:[Inject Named("bookmarkUserModelForSelf")]
    lateinit var bookmarkUserModelForSelf: BookmarkUserModel

    @field:[Inject Named("bookmarkUserModelForOther")]
    lateinit var bookmarkUserModelForOther: BookmarkUserModel

    @Inject
    lateinit var userModel: UserModel

    lateinit private var mUserId: String

    lateinit private var mListAdapter: BookmarkListAdapter

    lateinit private var mCompositeSubscription: CompositeSubscription

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
        App.graph.inject(this)

        mListAdapter = BookmarkListAdapter(activity, R.layout.list_item_bookmark)
        mIsOwner = arguments.getBoolean(ARG_OWNER_FLAG)
        mUserId = if (mIsOwner) userModel.userEntity!!.id else arguments.getString(ARG_USER_ID)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentListBinding.inflate(inflater, container, false)

        val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
        binding.fragmentListList.addFooterView(footerView, null, false)
        footerView.hide()

        binding.fragmentListList.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                // 一番下までスクロールしたら次ページの読み込みを開始
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val bookmarkUserModel = getBookmarkModel()
                    // 読込中以外、かつFooterViewが設定されている場合 = 次の読み込み対象が存在する場合、次ページ分をFetch.
                    if (!bookmarkUserModel.isBusy && 0 < binding.fragmentListList.footerViewsCount) {
                        bookmarkUserModel.fetch(mUserId, mListAdapter.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

        binding.fragmentListList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(BookmarkListItemClickedEvent(bookmarkEntity))
        }

        binding.fragmentListList.adapter = mListAdapter

        binding.fragmentListRefresh.setColorSchemeResources(R.color.pull_to_refresh_1,
                R.color.pull_to_refresh_2,
                R.color.pull_to_refresh_3)

        binding.fragmentListViewEmpty.text = getString(R.string.message_text_empty_bookmark_user)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        val bookmarkUserModel = getBookmarkModel()

        // Model内のユーザーIDと表示対象のユーザーIDが同じかチェックする
        if (bookmarkUserModel.isSameUser(mUserId)) {

            // 同じ場合は再表示する
            val displayedCount = mListAdapter.count

            if (displayedCount != bookmarkUserModel.bookmarkList.size) {
                // 表示済の件数とModel内で保持している件数をチェックし、
                // 差分があれば未表示のブックマークがあるのでリストに表示する
                displayListContents(binding.fragmentListList)
                binding.fragmentListProgressList.hide()
            } else if (displayedCount === 0) {
                // 1件も表示していなければブックマーク情報をRSSから取得する
                bookmarkUserModel.fetch(mUserId)
                binding.fragmentListProgressList.show()
            }
        } else {

            // Model内の情報と表示対象のユーザーIDが異なる場合は
            // 他人のブックマークを表示するので1件目から再取得する
            bookmarkUserModel.fetch(mUserId)
            binding.fragmentListProgressList.show()
        }

        binding.fragmentListViewEmpty.hide()

        // Pull to refreshのイベントをセット
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription.add(RxSwipeRefreshLayout.refreshes(binding.fragmentListRefresh).subscribe {
            // 上から引っ張りきったらbookmarkの更新を行う
            bookmarkUserModel.fetch(mUserId)
        })
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription.unsubscribe()

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        // Pull to Refresh中であれば解除する
        if (binding.fragmentListRefresh.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(binding.fragmentListRefresh).call(false)
        }
    }

    @Subscribe
    fun subscribe(event: ReadAfterFilterChangedEvent) {

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)
        if (binding.fragmentListList.footerViewsCount == 0) {
            val footerView = View.inflate(context, R.layout.list_fotter_loading, null)
            binding.fragmentListList.addFooterView(footerView, null, false)
            footerView.hide()
        }
        bookmarkUserModelForSelf.fetch(mUserId, event.type)
    }

    /**
     * ブックマーク情報のロード完了イベント
     */
    @Subscribe
    fun subscribe(event: BookmarkUserLoadedEvent) {

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        when (event.status) {
            LoadedEventStatus.OK -> {
                // 正常に完了した場合、リストに追加して表示を更新
                displayListContents(binding.fragmentListList)
            }
            LoadedEventStatus.NOT_FOUND -> {
                // 読込結果がなかった場合はFooterViewを非表示にする
                if (0 < binding.fragmentListList.footerViewsCount) {
                    val footerView = binding.root.findViewById(R.id.list_footer_loading_layout)
                    binding.fragmentListList.removeFooterView(footerView)
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
        binding.fragmentListViewEmpty.toggle(mListAdapter.isEmpty)

        // プログレスを非表示にする
        binding.fragmentListProgressList.hide()

        // Pull to refresh中だった場合は解除する
        if (binding.fragmentListRefresh.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(binding.fragmentListRefresh).call(false)
        }
    }

    /**
     * ListViewのコンテンツを表示する
     */
    private fun displayListContents(listView: ListView) {

        // コンテンツを表示する
        mListAdapter.apply {
            val bookmarkUserModel = getBookmarkModel()
            clear()
            addAll(bookmarkUserModel.bookmarkList)
            notifyDataSetChanged()
        }

        // FooterViewを表示する
        if (0 < listView.footerViewsCount) {
            listView.findViewById(R.id.list_footer_loading_layout).show()
        }
    }

    /**
     * 表示に使用するブックマークモデルを取得する
     */
    private fun getBookmarkModel(): BookmarkUserModel {
        return if (mIsOwner) bookmarkUserModelForSelf else bookmarkUserModelForOther
    }
}
