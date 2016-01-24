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
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapter(context: Context,
                          resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView?.apply {
            val binding = DataBindingUtil.getBinding<ListItemBookmarkBinding>(this)
            bindEntity(binding, getItem(position))
        } ?: let {
            val binding = ListItemBookmarkBinding.inflate(mLayoutInflater, parent, false)
            bindEntity(binding, getItem(position))
            return binding.root
        }

        return view
    }

    val nextIndex: Int
        get() {
            return count + 1
        }

    private fun bindEntity(binding: ListItemBookmarkBinding, bookmarkEntity: BookmarkEntity) {

        binding.let {
            it.bookmarkEntity = bookmarkEntity

            it.listItemBookmarkTextAddBookmarkTiming.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)

            Picasso.with(context)
                    .load(BookmarkUtil.getIconImageUrlFromId(bookmarkEntity.creator))
                    .transform(RoundedTransformation())
                    .into(it.listItemBookmarkImageUserIcon)
        }

        binding.listItemBookmarkLayoutBookmark.let {
            it.bookmarkEntity = bookmarkEntity
            if (bookmarkEntity.description.isEmpty()) {
                it.layoutBookmarkTextDescription.hide()
            }

            Picasso.with(context)
                    .load(bookmarkEntity.bookmarkIconUrl)
                    .transform(RoundedTransformation())
                    .into(it.layoutBookmarkImageArticleIcon)
        }
    }
}
