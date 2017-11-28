package me.rei_m.hbfavmaterial.presentation.widget.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.databinding.ListItemBookmarkBinding
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.BookmarkListItemViewModel

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapter(context: Context,
                          private val injector: Injector,
                          private val bookmarkList: ObservableArrayList<BookmarkEntity>) : ArrayAdapter<BookmarkEntity>(context, 0, bookmarkList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val bookmarkListChangedCallback = object : ObservableList.OnListChangedCallback<ObservableArrayList<BookmarkEntity>>() {
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
    }

    init {
        bookmarkList.addOnListChangedCallback(bookmarkListChangedCallback)
    }

    fun releaseCallback() {
        bookmarkList.removeOnListChangedCallback(bookmarkListChangedCallback)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val binding: ListItemBookmarkBinding
        if (convertView == null) {
            binding = ListItemBookmarkBinding.inflate(inflater, parent, false)
            binding.viewModel = injector.bookmarkListItemViewModel()
        } else {
            binding = DataBindingUtil.getBinding(convertView)
        }

        binding.viewModel!!.bookmark.set(getItem(position))
        // 即座に描画しないと上へスクロールした時にカクつく
        binding.executePendingBindings()

        return binding.root
    }

    interface Injector {
        fun bookmarkListItemViewModel(): BookmarkListItemViewModel
    }
}
