package me.rei_m.hbfavmaterial.views.widgets.list

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.squareup.picasso.Picasso

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.bookmark.BookmarkLayout
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * ブックマーク一覧のアイテムを表示するレイアウト.
 */
public class BookmarkItemLayout : RelativeLayout {

    companion object {
        private class ViewHolder(val name: AppCompatTextView,
                                 val iconImage: AppCompatImageView,
                                 val bookmarkLayout: BookmarkLayout,
                                 val timing: AppCompatTextView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(
                findViewById(R.id.list_item_bookmark_text_user_name) as AppCompatTextView,
                findViewById(R.id.list_item_bookmark_image_user_icon) as AppCompatImageView,
                findViewById(R.id.list_item_bookmark_layout_bookmark) as BookmarkLayout,
                findViewById(R.id.list_item_bookmark_text_add_bookmark_timing) as AppCompatTextView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            name.text = bookmarkEntity.creator
            Picasso.with(context)
                    .load(bookmarkEntity.bookmarkIconUrl)
                    .transform(RoundedTransformation())
                    .into(iconImage)
            bookmarkLayout.bindView(bookmarkEntity)
            timing.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)
        }
    }
}