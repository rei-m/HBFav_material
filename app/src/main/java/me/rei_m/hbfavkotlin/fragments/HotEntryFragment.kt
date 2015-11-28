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
import me.rei_m.hbfavkotlin.events.HotEntryLoadedEvent
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.HotEntryModel
import me.rei_m.hbfavkotlin.views.adapters.EntryListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavkotlin.events.HotEntryLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class HotEntryFragment : Fragment() {

    private var mListAdapter: EntryListAdapter? = null

    private var mCompositeSubscription: CompositeSubscription? = null

    companion object {
        fun newInstance(): HotEntryFragment {
            return HotEntryFragment()
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

        val hotEntryModel = ModelLocator.get(ModelTag.HOT_ENTRY) as HotEntryModel

        val displayedCount = mListAdapter?.count!!

        if (displayedCount != hotEntryModel.entryList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            mListAdapter?.clear()
            mListAdapter?.addAll(hotEntryModel.entryList)
            mListAdapter?.notifyDataSetChanged()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのブックマーク情報を取得する
            hotEntryModel.fetch()
        }

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription?.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            hotEntryModel.fetch()
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
    public fun onHotEntryLoadedEvent(event: HotEntryLoadedEvent) {
        when (event.type) {
            HotEntryLoadedEvent.Companion.Type.COMPLETE -> {
                val hotEntryModel = ModelLocator.get(ModelTag.HOT_ENTRY) as HotEntryModel
                mListAdapter?.clear()
                mListAdapter?.addAll(hotEntryModel.entryList)
                mListAdapter?.notifyDataSetChanged()
            }
            HotEntryLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }

        val swipeRefreshLayout = view.findViewById(R.id.refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }
}
