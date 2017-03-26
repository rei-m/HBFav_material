package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.constant.BookmarkCommentFilter
import me.rei_m.hbfavmaterial.constant.FilterItem
import me.rei_m.hbfavmaterial.di.BookmarkedUsersFragmentComponent
import me.rei_m.hbfavmaterial.di.BookmarkedUsersFragmentModule
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.view.adapter.UserListAdapter
import javax.inject.Inject

/**
 * 対象の記事をブックマークしているユーザの一覧を表示するFragment.
 */
class BookmarkedUsersFragment : BaseFragment(), BookmarkedUsersContact.View {

    companion object {

        private const val ARG_BOOKMARK = "ARG_BOOKMARK"

        private const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(bookmarkEntity: BookmarkEntity): BookmarkedUsersFragment {
            return BookmarkedUsersFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOKMARK, bookmarkEntity)
                }
            }
        }
    }

    @Inject
    lateinit var presenter: BookmarkedUsersContact.Actions

    @Inject
    lateinit var activityNavigator: ActivityNavigator

    private var disposable: CompositeDisposable? = null

    private var listener: OnFragmentInteractionListener? = null

    private val bookmarkEntity: BookmarkEntity by lazy {
        arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    private val listAdapter: UserListAdapter by lazy {
        UserListAdapter(activity, R.layout.list_item_user)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bookmarkCommentFilter = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(KEY_FILTER_TYPE) as BookmarkCommentFilter
        } else {
            BookmarkCommentFilter.ALL
        }
        presenter.onCreate(this, bookmarkEntity, bookmarkCommentFilter)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        disposable = CompositeDisposable()

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            presenter.onClickUser(bookmarkEntity)
        }

        listView.adapter = listAdapter

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            setColorSchemeResources(R.color.pull_to_refresh_1,
                    R.color.pull_to_refresh_2,
                    R.color.pull_to_refresh_3)
        }

        with(view.findViewById(R.id.fragment_list_view_empty) as TextView) {
            text = getString(R.string.message_text_empty_user)
        }

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        disposable?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
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

        disposable?.dispose()
        disposable = null

        val view = view ?: return

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).accept(false)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_bookmarked_users, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val id = item.itemId

        if (id == android.R.id.home) {
            return super.onOptionsItemSelected(item)
        }

        val commentFilter = FilterItem.forMenuId(id) as BookmarkCommentFilter

        presenter.onOptionItemSelected(commentFilter)

        listener?.onChangeFilter(commentFilter)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, presenter.bookmarkCommentFilter)
    }

    override fun showUserList(bookmarkList: List<BookmarkEntity>) {

        val view = view ?: return

        with(listAdapter) {
            clear()
            addAll(bookmarkList)
            notifyDataSetChanged()
        }

        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        listView.setSelection(0)

        view.findViewById(R.id.fragment_list_list).show()

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).accept(false)
            }
        }

        listener?.onChangeFilter(presenter.bookmarkCommentFilter)
    }

    override fun hideUserList() {
        val view = view ?: return
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        listView.setSelection(0)
        listView.hide()
    }

    override fun showNetworkErrorMessage() {
        with(activity as AppCompatActivity) {
            showSnackbarNetworkError()
        }
    }

    override fun showProgress() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_progress_list).show()
    }

    override fun hideProgress() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_progress_list).hide()
    }

    override fun showEmpty() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_view_empty).show()
    }

    override fun hideEmpty() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_view_empty).hide()
    }

    override fun navigateToOthersBookmark(bookmarkEntity: BookmarkEntity) {
        activityNavigator.navigateToOthersBookmark(activity, bookmarkEntity.creator)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .plus(BookmarkedUsersFragmentModule(context))
                .inject(this)
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter)
    }

    interface Injector {
        fun plus(fragmentModule: BookmarkedUsersFragmentModule?): BookmarkedUsersFragmentComponent
    }
}
