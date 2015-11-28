package me.rei_m.hbfavkotlin.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.events.NewEntryLoadedEvent
import me.rei_m.hbfavkotlin.extensions.hide
import me.rei_m.hbfavkotlin.extensions.show
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.NewEntryModel
import me.rei_m.hbfavkotlin.views.adapters.EntryListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavkotlin.events.HotEntryLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class NewEntryFragment : Fragment() {

    private var mListAdapter: EntryListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {
        fun newInstance(): NewEntryFragment {
            return NewEntryFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = EntryListAdapter(activity, R.layout.list_item_entry)
    }

    override fun onDestroy() {
        super.onDestroy()
        mListAdapter = null
        mCompositeSubscription = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_bookmark_list, container, false)

        val listView = view.findViewById(R.id.list_bookmark) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            //            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            //            EventBusHolder.EVENT_BUS.post(BookmarkListClickEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val newEntryModel = ModelLocator.get(ModelTag.NEW_ENTRY) as NewEntryModel

        val displayedCount = mListAdapter?.count!!

        if (displayedCount != newEntryModel.entryList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            mListAdapter?.clear()
            mListAdapter?.addAll(newEntryModel.entryList)
            mListAdapter?.notifyDataSetChanged()
            view.findViewById(R.id.progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのブックマーク情報を取得する
            newEntryModel.fetch()
            view.findViewById(R.id.progress_list).show()
        }

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            newEntryModel.fetch()
        }))
    }

    override fun onPause() {
        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription?.unsubscribe()

        super.onPause()
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onNewEntryLoadedEvent(event: NewEntryLoadedEvent) {
        when (event.type) {
            NewEntryLoadedEvent.Companion.Type.COMPLETE -> {
                val newEntryModel = ModelLocator.get(ModelTag.NEW_ENTRY) as NewEntryModel
                mListAdapter?.clear()
                mListAdapter?.addAll(newEntryModel.entryList)
                mListAdapter?.notifyDataSetChanged()
            }
            NewEntryLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }

        view.findViewById(R.id.progress_list).hide()

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }
}
