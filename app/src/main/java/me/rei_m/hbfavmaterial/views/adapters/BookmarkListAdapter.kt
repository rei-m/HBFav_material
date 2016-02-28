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
class BookmarkListAdapter(context: Context,
                          resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    companion object {
        private val BOOKMARK_COUNT_PER_PAGE = 20
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_bookmark, null)

        (view as BookmarkItemLayout).bindView(getItem(position))

        return view
    }

    val nextIndex: Int
        get() {
            if (BOOKMARK_COUNT_PER_PAGE <= (count + 1)) {
                val pageCnt = (count / BOOKMARK_COUNT_PER_PAGE)
                val mod = (count % BOOKMARK_COUNT_PER_PAGE)
                return if (mod == 0) {
                    pageCnt * BOOKMARK_COUNT_PER_PAGE + 1
                } else {
                    (pageCnt + 1) * BOOKMARK_COUNT_PER_PAGE + 1
                }
            } else {
                return BOOKMARK_COUNT_PER_PAGE + 1
            }
        }
}
