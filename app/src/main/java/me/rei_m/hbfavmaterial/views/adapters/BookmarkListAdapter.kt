package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.databinding.ListItemBookmarkBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapter(context: Context,
                          resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    companion object {
        private val BOOKMARK_COUNT_PER_PAGE = 20
    }

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val item = getItem(position)

        return convertView?.apply {
            DataBindingUtil.getBinding<ListItemBookmarkBinding>(this).apply {
                bookmarkEntity = item
                listItemBookmarkLayoutBookmark.bookmarkEntity = item
            }
        } ?: let {
            return ListItemBookmarkBinding.inflate(mLayoutInflater, parent, false).apply {
                bookmarkEntity = item
                listItemBookmarkLayoutBookmark.bookmarkEntity = item
            }.root
        }
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
