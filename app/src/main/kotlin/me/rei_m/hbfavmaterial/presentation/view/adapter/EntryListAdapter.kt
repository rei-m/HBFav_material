package me.rei_m.hbfavmaterial.presentation.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.databinding.ListItemEntryBinding
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
import me.rei_m.hbfavmaterial.presentation.viewmodel.EntryListItemViewModel

/**
 * エントリー一覧を管理するAdaptor.
 */
class EntryListAdapter(context: Context,
                       private val injector: Injector,
                       entryList: ObservableArrayList<EntryEntity>) : ArrayAdapter<EntryEntity>(context, 0, entryList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        entryList.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<BookmarkEntity>>() {
            override fun onItemRangeInserted(p0: ObservableArrayList<BookmarkEntity>?, p1: Int, p2: Int) {
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(p0: ObservableArrayList<BookmarkEntity>?, p1: Int, p2: Int) {
                notifyDataSetChanged()
            }

            override fun onItemRangeMoved(p0: ObservableArrayList<BookmarkEntity>?, p1: Int, p2: Int, p3: Int) {
                notifyDataSetChanged()
            }

            override fun onChanged(p0: ObservableArrayList<BookmarkEntity>?) {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(p0: ObservableArrayList<BookmarkEntity>?, p1: Int, p2: Int) {
                notifyDataSetChanged()
            }
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val binding: ListItemEntryBinding
        if (convertView == null) {
            binding = ListItemEntryBinding.inflate(inflater, parent, false)
            binding.viewModel = injector.entryListItemViewModel()
        } else {
            binding = DataBindingUtil.getBinding(convertView)
        }

        binding.viewModel.entry.set(getItem(position))
        binding.executePendingBindings()

        return binding.root
    }

    interface Injector {
        fun entryListItemViewModel(): EntryListItemViewModel
    }
}
