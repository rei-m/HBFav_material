package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.fragments.presenter.HotEntryContact
import me.rei_m.hbfavmaterial.fragments.presenter.HotEntryPresenter
import me.rei_m.hbfavmaterial.views.adapters.EntryListAdapter
import rx.subscriptions.CompositeSubscription

/**
 * HotEntryを一覧で表示するFragment.
 */
class HotEntryFragment : BaseFragment(), HotEntryContact.View {

    private lateinit var presenter: HotEntryPresenter

    private val listAdapter: EntryListAdapter by lazy {
        EntryListAdapter(activity, R.layout.list_item_entry)
    }

    private var subscription: CompositeSubscription? = null

    companion object {
        fun newInstance(): HotEntryFragment = HotEntryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = HotEntryPresenter(this, EntryTypeFilter.ALL)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val entryEntity = parent?.adapter?.getItem(position) as EntryEntity
            presenter.clickEntry(entryEntity)
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

        return view
    }

    override fun onResume() {
        super.onResume()
        subscription = CompositeSubscription()

        val view = view ?: return

        if (listAdapter.count === 0) {
            // 1件も表示していなければブックマーク情報をRSSから取得する
            presenter.initializeListContents()?.let {
                subscription?.add(it)
            }
        }

        // Pull to refreshのイベントをセット
        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        subscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            presenter.fetchListContents()?.let {
                subscription?.add(it)
            }
        })
    }

    override fun onPause() {
        super.onPause()
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_entry, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        val filter = EntryTypeFilter.forMenuId(item.itemId)
        presenter.toggleListContents(filter)?.let {
            subscription?.add(it)
        }

        return true
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

    override fun showEmpty() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_view_empty).show()
    }

    override fun hideEmpty() {
        val view = view ?: return
        view.findViewById(R.id.fragment_list_view_empty).hide()
    }
}
