package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.views.widgets.list.BookmarkItemLayout

/**
 * ブックマーク一覧を管理するAdaptor.
 */
public class BookmarkListAdapter constructor(context: Context,
                                             resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_bookmark, null)

        (view as BookmarkItemLayout).bindView(getItem(position))

        return view
    }

    public val nextIndex: Int
        get() {
            return count + 1
        }
}
