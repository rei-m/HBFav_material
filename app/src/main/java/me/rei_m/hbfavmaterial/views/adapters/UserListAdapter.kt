package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.databinding.ListItemUserBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity

/**
 * ユーザー一覧を管理するAdaptor.
 */
class UserListAdapter(context: Context,
                      resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        return convertView?.apply {
            DataBindingUtil.getBinding<ListItemUserBinding>(this).apply {
                bookmarkEntity = getItem(position)
            }
        } ?: let {
            return ListItemUserBinding.inflate(mLayoutInflater, parent, false).apply {
                bookmarkEntity = getItem(position)
            }.root
        }
    }
}
