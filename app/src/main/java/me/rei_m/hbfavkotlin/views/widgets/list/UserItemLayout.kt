package me.rei_m.hbfavkotlin.views.widgets.list

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.utils.BookmarkUtil
import me.rei_m.hbfavkotlin.views.widgets.graphics.RoundedTransformation

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
                findViewById(R.id.text_user_name) as AppCompatTextView,
                findViewById(R.id.image_user_icon) as AppCompatImageView,
                findViewById(R.id.text_add_bookmark_timing) as AppCompatTextView,
                findViewById(R.id.text_bookmark_description) as AppCompatTextView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder

        holder.name.text = bookmarkEntity.creator
        holder.timing.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)

        Picasso.with(context)
                .load(BookmarkUtil.getIconImageUrlFromId(bookmarkEntity.creator))
                .transform(RoundedTransformation())
                .into(holder.iconImage)

        holder.description.text = bookmarkEntity.description
    }
}