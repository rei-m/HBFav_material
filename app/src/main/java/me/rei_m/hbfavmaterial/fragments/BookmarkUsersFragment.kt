package me.rei_m.hbfavmaterial.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentListBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.network.UserRegisterBookmarkLoadedEvent
import me.rei_m.hbfavmaterial.events.ui.BookmarkUsersFilteredEvent
import me.rei_m.hbfavmaterial.events.ui.UserListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.UserRegisterBookmarkModel
import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.FilterType
import me.rei_m.hbfavmaterial.views.adapters.UserListAdapter
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * 対象の記事をブックマークしているユーザの一覧を表示するFragment.
 */
class BookmarkUsersFragment : Fragment() {

    @Inject
    lateinit var userRegisterBookmarkModel: UserRegisterBookmarkModel

    private val mBookmarkEntity: BookmarkEntity by lazy {
        arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    private val mListAdapter: UserListAdapter by lazy {
        UserListAdapter(activity, R.layout.list_item_user)
    }

    private var mFilterType: FilterType = FilterType.ALL

    lateinit private var mCompositeSubscription: CompositeSubscription

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        private val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(bookmarkEntity: BookmarkEntity): BookmarkUsersFragment {
            return BookmarkUsersFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOKMARK, bookmarkEntity)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)
        
        if (savedInstanceState != null) {
            mFilterType = savedInstanceState.getSerializable(KEY_FILTER_TYPE) as FilterType
        } else {
            mFilterType = FilterType.ALL
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentListBinding.inflate(inflater, container, false)

        binding.fragmentListList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(UserListItemClickedEvent(bookmarkEntity))
        }

        binding.fragmentListList.adapter = mListAdapter

        binding.fragmentListRefresh.setColorSchemeResources(R.color.pull_to_refresh_1,
                R.color.pull_to_refresh_2,
                R.color.pull_to_refresh_3)

        binding.fragmentListViewEmpty.text = getString(R.string.message_text_empty_user)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        val bookmarkUrl = mBookmarkEntity.articleEntity.url

        // Model内のURLと表示対象のURLが同じかチェックする
        if (userRegisterBookmarkModel.isSameUrl(bookmarkUrl)) {

            // 同じ場合は再表示する
            val displayedCount = mListAdapter.count

            if (displayedCount != userRegisterBookmarkModel.bookmarkList.size) {
                displayListContents(binding.fragmentListList)
                binding.fragmentListProgressList.hide()
            } else if (displayedCount === 0) {
                userRegisterBookmarkModel.fetch(bookmarkUrl)
                binding.fragmentListProgressList.show()
            }
        } else {

            // Model内の情報と表示対象のURLが異なる場合は
            // 異なる記事のブックマークユーザーを表示するので1件目から再取得する
            userRegisterBookmarkModel.fetch(bookmarkUrl)
            binding.fragmentListProgressList.show()
        }

        binding.fragmentListViewEmpty.hide()

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription.add(RxSwipeRefreshLayout.refreshes(binding.fragmentListRefresh).subscribe {
            userRegisterBookmarkModel.fetch(mBookmarkEntity.articleEntity.url)
        })
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription.unsubscribe()

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        if (binding.fragmentListRefresh.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(binding.fragmentListRefresh).call(false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, mFilterType)
    }

    /**
     * ブックマークユーザ情報のロード完了イベント
     */
    @Subscribe
    fun subscribe(event: UserRegisterBookmarkLoadedEvent) {

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        when (event.status) {
            LoadedEventStatus.OK -> {
                displayListContents(binding.fragmentListList)
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

        binding.fragmentListProgressList.hide()

        if (binding.fragmentListRefresh.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(binding.fragmentListRefresh).call(false)
        }
    }

    @Subscribe
    fun subscribe(event: BookmarkUsersFilteredEvent) {
        mFilterType = event.filterType

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        displayListContents(binding.fragmentListList)
    }

    private fun displayListContents(listView: ListView) {
        with(mListAdapter) {
            clear()
            if (mFilterType == FilterType.COMMENT) {
                addAll(userRegisterBookmarkModel.bookmarkList.filter { bookmark -> bookmark.description.isNotEmpty() })
            } else {
                addAll(userRegisterBookmarkModel.bookmarkList)
            }
            notifyDataSetChanged()
        }
        listView.setSelection(0)
    }
}
