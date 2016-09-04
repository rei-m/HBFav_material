package me.rei_m.hbfavmaterial.presentation.view.widget.list

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.presentation.util.BookmarkUtil
import me.rei_m.hbfavmaterial.presentation.view.widget.graphics.RoundedTransformation

/**
 * ブックマークしたユーザーの一覧のアイテムを表示するレイアウト.
 */
class UserItemLayout : RelativeLayout {

    companion object {
        private class ViewHolder(val name: AppCompatTextView,
                                 val iconImage: AppCompatImageView,
                                 val timing: AppCompatTextView,
                                 val description: AppCompatTextView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(
                findViewById(R.id.list_item_user_text_name) as AppCompatTextView,
                findViewById(R.id.list_item_user_image_icon) as AppCompatImageView,
                findViewById(R.id.list_item_user_text_add_bookmark_timing) as AppCompatTextView,
                findViewById(R.id.list_item_user_text_bookmark_description) as AppCompatTextView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            name.text = bookmarkEntity.creator
            timing.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)

            Picasso.with(context)
                    .load(BookmarkUtil.getIconImageUrlFromId(bookmarkEntity.creator))
                    .transform(RoundedTransformation())
                    .into(iconImage)

            description.text = bookmarkEntity.description
        }
    }
}
