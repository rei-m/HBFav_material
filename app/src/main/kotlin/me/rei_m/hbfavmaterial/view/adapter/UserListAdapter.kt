package me.rei_m.hbfavmaterial.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entitiy.BookmarkEntity
import me.rei_m.hbfavmaterial.view.widget.list.UserItemLayout

/**
 * ユーザー一覧を管理するAdaptor.
 */
class UserListAdapter(context: Context,
                      resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_user, null)

        (view as UserItemLayout).bindView(getItem(position))

        return view
    }
}
