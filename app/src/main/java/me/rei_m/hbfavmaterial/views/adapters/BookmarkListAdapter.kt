package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.databinding.ListItemBookmarkBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

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

        return convertView?.apply {
            val binding = DataBindingUtil.getBinding<ListItemBookmarkBinding>(this)
            bindEntity(binding, getItem(position))
        } ?: let {
            val binding = ListItemBookmarkBinding.inflate(mLayoutInflater, parent, false)
            bindEntity(binding, getItem(position))
            return binding.root
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

    private fun bindEntity(binding: ListItemBookmarkBinding, item: BookmarkEntity) {

        with(binding) {
            bookmarkEntity = item

            listItemBookmarkTextAddBookmarkTiming.text = BookmarkUtil.getPastTimeString(item.date)

            Picasso.with(context)
                    .load(BookmarkUtil.getIconImageUrlFromId(item.creator))
                    .transform(RoundedTransformation())
                    .into(listItemBookmarkImageUserIcon)
        }

        with(binding.listItemBookmarkLayoutBookmark) {
            layoutBookmarkTextDescription.toggle(!item.description.isEmpty())
            Picasso.with(context)
                    .load(item.articleEntity.iconUrl)
                    .transform(RoundedTransformation())
                    .into(layoutBookmarkImageArticleIcon)
        }
    }
}
