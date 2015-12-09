package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.events.EntryCategoryChangedEvent
import me.rei_m.hbfavmaterial.events.EntryListItemClickedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.HotEntryLoadedEvent
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.HotEntryModel
import me.rei_m.hbfavmaterial.views.adapters.EntryListAdapter
import rx.subscriptions.CompositeSubscription
import me.rei_m.hbfavmaterial.events.HotEntryLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

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

        val view = inflater!!.inflate(R.layout.fragment_list, container, false)

        val listView = view.findViewById(R.id.fragment_list_list) as ListView

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val entryEntity = parent?.adapter?.getItem(position) as EntryEntity
            EventBusHolder.EVENT_BUS.post(EntryListItemClickedEvent(entryEntity))
        }

        listView.adapter = mListAdapter

        return view
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val hotEntryModel = ModelLocator.get(ModelTag.HOT_ENTRY) as HotEntryModel

        val displayedCount = mListAdapter?.count!!

        if (displayedCount != hotEntryModel.entryList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のエントリがあるのでリストに表示する
            mListAdapter?.clear()
            mListAdapter?.addAll(hotEntryModel.entryList)
            mListAdapter?.notifyDataSetChanged()
            view.findViewById(R.id.fragment_list_progress_list).hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければエントリ情報を取得する
            hotEntryModel.fetch(hotEntryModel.entryType)
            view.findViewById(R.id.fragment_list_progress_list).show()
        }

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription!!.add(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout).subscribe({
            hotEntryModel.fetch(hotEntryModel.entryType)
        }))
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
        mCompositeSubscription?.unsubscribe()
    }

    @Subscribe
    public fun onEntryCategoryChangedEvent(event: EntryCategoryChangedEvent) {
        if (event.target == EntryCategoryChangedEvent.Companion.Target.HOT) {
            val hotEntryModel = ModelLocator.get(ModelTag.HOT_ENTRY) as HotEntryModel
            hotEntryModel.fetch(event.type)
        }
    }

    @Subscribe
    public fun onHotEntryLoadedEvent(event: HotEntryLoadedEvent) {
        when (event.type) {
            HotEntryLoadedEvent.Companion.Type.COMPLETE -> {
                val hotEntryModel = ModelLocator.get(ModelTag.HOT_ENTRY) as HotEntryModel
                mListAdapter?.clear()
                mListAdapter?.addAll(hotEntryModel.entryList)
                mListAdapter?.notifyDataSetChanged()
            }
            HotEntryLoadedEvent.Companion.Type.ERROR -> {
                val thisActivity = activity as AppCompatActivity
                thisActivity.showSnackbarNetworkError(view)
            }
        }

        view.findViewById(R.id.fragment_list_progress_list).hide()

        val swipeRefreshLayout = view.findViewById(R.id.fragment_list_refresh) as SwipeRefreshLayout
        if (swipeRefreshLayout.isRefreshing) {
            RxSwipeRefreshLayout.refreshing(swipeRefreshLayout).call(false)
        }
    }
}
