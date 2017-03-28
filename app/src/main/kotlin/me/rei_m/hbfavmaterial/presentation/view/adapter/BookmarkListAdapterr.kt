package me.rei_m.hbfavmaterial.presentation.view.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.databinding.ListItemBookmarkBinding
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.viewmodel.BookmarkListItemViewModel

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapterr(context: Context,
                           bookmarkList: ObservableArrayList<BookmarkEntity>,
                           val countPerPage: Int) : ArrayAdapter<BookmarkEntity>(context, 0) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        bookmarkList.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableArrayList<BookmarkEntity>>() {
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

        val binding: ListItemBookmarkBinding
        if (convertView == null) {
            binding = ListItemBookmarkBinding.inflate(inflater, parent, false)
            // viewModelをセット
        } else {
            binding = DataBindingUtil.getBinding(convertView)
        }

        val bookmark = getItem(position)
        // ViewModelにアイテムをセット

        return binding.root
    }

    val nextIndex: Int
        get() {
            val pageCnt = (count / countPerPage)
            val mod = (count % countPerPage)

            return if (mod == 0) {
                pageCnt * countPerPage + 1
            } else {
                (pageCnt + 1) * countPerPage + 1
            }
        }

    interface Injector {
        fun bookmarkListItemViewModel(): BookmarkListItemViewModel
    }
}
