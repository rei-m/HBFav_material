package me.rei_m.hbfavmaterial.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.di.NewEntryFragmentComponent
import me.rei_m.hbfavmaterial.di.NewEntryFragmentModule
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.view.adapter.BookmarkPagerAdaptor
import me.rei_m.hbfavmaterial.presentation.view.adapter.EntryListAdapter
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * 新着Entryを一覧で表示するFragment.
 */
class NewEntryFragment() : BaseFragment(),
        NewEntryContact.View,
        MainPageFragment {

    companion object {

        private const val ARG_PAGE_INDEX = "ARG_PAGE_INDEX"

        private const val KEY_FILTER_TYPE = "KEY_FILTER_TYPE"

        fun newInstance(pageIndex: Int): NewEntryFragment = NewEntryFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE_INDEX, pageIndex)
            }
        }
    }

    @Inject
    lateinit var presenter: NewEntryContact.Actions

    @Inject
    lateinit var activityNavigator: ActivityNavigator

    private var listener: OnFragmentInteractionListener? = null

    private val listAdapter: EntryListAdapter by lazy {
        EntryListAdapter(activity, R.layout.list_item_entry)
    }

    private var subscription: CompositeSubscription? = null

    override val pageIndex: Int
        get() = arguments.getInt(ARG_PAGE_INDEX)

    override val pageTitle: String
        get() = BookmarkPagerAdaptor.Page.values()[pageIndex].title(getAppContext(), presenter.entryTypeFilter.title(getAppContext()))

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val entryTypeFilter = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(NewEntryFragment.KEY_FILTER_TYPE) as EntryTypeFilter
        } else {
            EntryTypeFilter.ALL
        }

        presenter.onCreate(this, entryTypeFilter)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        subscription = CompositeSubscription()

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val entryEntity = parent?.adapter?.getItem(position) as EntryEntity
            presenter.onClickEntry(entryEntity)
        }

        listView.adapter = listAdapter

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            setColorSchemeResources(R.color.pull_to_refresh_1,
                    R.color.pull_to_refresh_2,
                    R.color.pull_to_refresh_3)
        }

        with(view.findViewById(R.id.fragment_list_view_empty) as TextView) {
            text = getString(R.string.message_text_empty_entry)
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

        // Pull to Refresh中であれば解除する
        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_entry, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        item ?: return false

        val filter = EntryTypeFilter.forMenuId(item.itemId)

        presenter.onOptionItemSelected(filter)

        listener?.onChangeFilter(pageTitle)

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable(KEY_FILTER_TYPE, presenter.entryTypeFilter)
    }

    override fun showEntryList(entryList: List<EntryEntity>) {
        val view = view ?: return

        with(listAdapter) {
            clear()
            addAll(entryList)
            notifyDataSetChanged()
        }

        view.findViewById(R.id.fragment_list_list).show()

        with(view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout) {
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    override fun hideEntryList() {
        val view = view ?: return
        val listView = view.findViewById(R.id.fragment_list_list) as ListView
        listView.setSelection(0)
        listView.hide()
    }

    override fun showNetworkErrorMessage() {
        (activity as AppCompatActivity).showSnackbarNetworkError()
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

    override fun navigateToBookmark(entryEntity: EntryEntity) {
        activityNavigator.navigateToBookmark(activity, entryEntity)
    }

    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .plus(NewEntryFragmentModule(context))
                .inject(this)
    }

    interface Injector {
        fun plus(fragmentModule: NewEntryFragmentModule): NewEntryFragmentComponent
    }

    interface OnFragmentInteractionListener {
        fun onChangeFilter(newPageTitle: String)
    }
}
