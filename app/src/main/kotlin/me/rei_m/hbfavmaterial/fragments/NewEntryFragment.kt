package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.network.NewEntryLoadedEvent
import me.rei_m.hbfavmaterial.events.ui.EntryCategoryChangedEvent
import me.rei_m.hbfavmaterial.events.ui.EntryListItemClickedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.NewEntryModel
import me.rei_m.hbfavmaterial.views.adapters.EntryListAdapter
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * 新着Entryを一覧で表示するFragment.
 */
class NewEntryFragment : BaseFragment() {

    @Inject
    lateinit var newEntryModel: NewEntryModel

    private val mListAdapter: EntryListAdapter by lazy {
        EntryListAdapter(activity, R.layout.list_item_entry)
    }

    lateinit private var mCompositeSubscription: CompositeSubscription

    companion object {
        fun newInstance(): NewEntryFragment {
            return NewEntryFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val entryEntity = parent?.adapter?.getItem(position) as EntryEntity
            EventBusHolder.EVENT_BUS.post(EntryListItemClickedEvent(entryEntity))
        }

        listView.adapter = mListAdapter

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
        EventBusHolder.EVENT_BUS.register(this)

        val view = view ?: return

        val displayedCount = mListAdapter.count

        if (displayedCount != newEntryModel.entryList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のエントリがあるのでリストに表示する
            displayListContents(view.findViewById(R.id.fragment_list_list) as ListView)
            view.findViewById(R.id.fragment_list_progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのエントリ情報を取得する
            newEntryModel.fetch(newEntryModel.entryType)
            view.findViewById(R.id.fragment_list_progress_list).show()
        }

        view.findViewById(R.id.fragment_list_view_empty).hide()

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe {
            newEntryModel.fetch(newEntryModel.entryType)
        })
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription.unsubscribe()
    }

    @Subscribe
    fun subscribe(event: EntryCategoryChangedEvent) {
        if (event.target == EntryCategoryChangedEvent.Target.NEW) {
            newEntryModel.fetch(event.typeFilter)
        }
    }

    @Subscribe
    fun subscribe(event: NewEntryLoadedEvent) {

        val view = view ?: return

        when (event.status) {
            LoadedEventStatus.OK -> {
                displayListContents(view.findViewById(R.id.fragment_list_list) as ListView)
            }
            LoadedEventStatus.ERROR -> {
                val thisActivity = activity as AppCompatActivity
//                thisActivity.showSnackbarNetworkError(view)
            }
            else -> {

            }
        }

        // リストが空の場合はEmptyViewを表示する
        view.findViewById(R.id.fragment_list_view_empty).toggle(mListAdapter.isEmpty)

        view.findViewById(R.id.fragment_list_progress_list).hide()

        with(view.findViewById(R.id.fragment_list_refresh)) {
            this as SwipeRefreshLayout
            if (isRefreshing) {
                RxSwipeRefreshLayout.refreshing(this).call(false)
            }
        }
    }

    private fun displayListContents(listView: ListView) {
        with(mListAdapter) {
            clear()
            addAll(newEntryModel.entryList)
            notifyDataSetChanged()
        }
        listView.setSelection(0)
    }
}
