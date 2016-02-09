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
class NewEntryFragment : Fragment() {

    @Inject
    lateinit var newEntryModel: NewEntryModel

    lateinit private var mListAdapter: EntryListAdapter

    lateinit private var mCompositeSubscription: CompositeSubscription

    companion object {
        fun newInstance(): NewEntryFragment {
            return NewEntryFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)

        mListAdapter = EntryListAdapter(activity, R.layout.list_item_entry)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentListBinding.inflate(inflater, container, false)

        binding.fragmentListList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val entryEntity = parent?.adapter?.getItem(position) as EntryEntity
            EventBusHolder.EVENT_BUS.post(EntryListItemClickedEvent(entryEntity))
        }

        binding.fragmentListList.adapter = mListAdapter

        binding.fragmentListRefresh.setColorSchemeResources(R.color.pull_to_refresh_1,
                R.color.pull_to_refresh_2,
                R.color.pull_to_refresh_3)

        binding.fragmentListViewEmpty.text = getString(R.string.message_text_empty_entry)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        val displayedCount = mListAdapter.count

        if (displayedCount != newEntryModel.entryList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のエントリがあるのでリストに表示する
            displayListContents(binding.fragmentListList)
            binding.fragmentListProgressList.hide()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのエントリ情報を取得する
            newEntryModel.fetch(newEntryModel.entryType)
            binding.fragmentListProgressList.show()
        }

        binding.fragmentListViewEmpty.hide()

        mCompositeSubscription = CompositeSubscription()
        mCompositeSubscription.add(RxSwipeRefreshLayout.refreshes(binding.fragmentListRefresh).subscribe {
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
        if (event.target == EntryCategoryChangedEvent.Companion.Target.NEW) {
            newEntryModel.fetch(event.type)
        }
    }

    @Subscribe
    fun subscribe(event: NewEntryLoadedEvent) {

        val binding = DataBindingUtil.getBinding<FragmentListBinding>(view)

        when (event.status) {
            LoadedEventStatus.OK -> {
                displayListContents(binding.fragmentListList)
            }
            LoadedEventStatus.ERROR -> {
                val thisActivity = activity as AppCompatActivity
                thisActivity.showSnackbarNetworkError(view)
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

    private fun displayListContents(listView: ListView) {
        with(mListAdapter) {
            clear()
            addAll(newEntryModel.entryList)
            notifyDataSetChanged()
        }
        listView.setSelection(0)
    }
}
