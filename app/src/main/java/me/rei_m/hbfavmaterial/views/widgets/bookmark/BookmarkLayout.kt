package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

class BookmarkLayout : RelativeLayout {

    companion object {
        private class ViewHolder(val description: AppCompatTextView,
                                 val title: AppCompatTextView,
                                 val iconImage: AppCompatImageView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(findViewById(R.id.text_bookmark_description) as AppCompatTextView,
                findViewById(R.id.text_bookmark_title) as AppCompatTextView,
                findViewById(R.id.image_article_icon) as AppCompatImageView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        if (bookmarkEntity.description.isEmpty()) {
            holder.description.visibility = GONE
        } else {
            holder.description.visibility = VISIBLE
            holder.description.text = bookmarkEntity.description
        }
        holder.title.text = bookmarkEntity.title
        Picasso.with(context)
                .load(bookmarkEntity.articleIconUrl)
                .transform(RoundedTransformation())
                .into(holder.iconImage)
    }
}