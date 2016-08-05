package me.rei_m.hbfavmaterial.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entitiy.BookmarkEntity
import me.rei_m.hbfavmaterial.view.widget.list.BookmarkItemLayout

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapter(context: Context,
                          resource: Int,
                          val countPerPage:Int ) : ArrayAdapter<BookmarkEntity>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_bookmark, null)

        (view as BookmarkItemLayout).bindView(getItem(position))

        return view
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
}
