/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

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
import me.rei_m.hbfavmaterial.model.entity.Bookmark
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.BookmarkListItemViewModel

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapter(context: Context,
                          private val injector: Injector,
                          private val bookmarkList: ObservableArrayList<Bookmark>) : ArrayAdapter<Bookmark>(context, 0, bookmarkList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val bookmarkListChangedCallback = object : ObservableList.OnListChangedCallback<ObservableArrayList<Bookmark>>() {
        override fun onItemRangeInserted(p0: ObservableArrayList<Bookmark>?, p1: Int, p2: Int) {
            notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(p0: ObservableArrayList<Bookmark>?, p1: Int, p2: Int) {
            notifyDataSetChanged()
        }

        override fun onItemRangeMoved(p0: ObservableArrayList<Bookmark>?, p1: Int, p2: Int, p3: Int) {
            notifyDataSetChanged()
        }

        override fun onChanged(p0: ObservableArrayList<Bookmark>?) {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(p0: ObservableArrayList<Bookmark>?, p1: Int, p2: Int) {
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
