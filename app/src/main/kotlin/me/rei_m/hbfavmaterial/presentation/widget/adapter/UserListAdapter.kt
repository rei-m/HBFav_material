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
import me.rei_m.hbfavmaterial.databinding.ListItemUserBinding
import me.rei_m.hbfavmaterial.model.entity.Bookmark
import me.rei_m.hbfavmaterial.model.entity.BookmarkUser
import me.rei_m.hbfavmaterial.viewmodel.widget.adapter.UserListItemViewModel

/**
 * ユーザー一覧を管理するAdaptor.
 */
class UserListAdapter(context: Context?,
                      private val injector: Injector,
                      private val bookmarkUserList: ObservableArrayList<BookmarkUser>) : ArrayAdapter<BookmarkUser>(context, 0, bookmarkUserList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val bookmarkUserListChangedCallback = object : ObservableList.OnListChangedCallback<ObservableArrayList<Bookmark>>() {
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
        bookmarkUserList.addOnListChangedCallback(bookmarkUserListChangedCallback)
    }

    fun releaseCallback() {
        bookmarkUserList.removeOnListChangedCallback(bookmarkUserListChangedCallback)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val binding: ListItemUserBinding
        if (convertView == null) {
            binding = ListItemUserBinding.inflate(inflater, parent, false)
            binding.viewModel = injector.userListItemViewModel()
        } else {
            binding = DataBindingUtil.getBinding(convertView)
        }

        binding.viewModel!!.bookmarkUser.set(getItem(position))
        binding.executePendingBindings()

        return binding.root
    }

    interface Injector {
        fun userListItemViewModel(): UserListItemViewModel
    }
}
