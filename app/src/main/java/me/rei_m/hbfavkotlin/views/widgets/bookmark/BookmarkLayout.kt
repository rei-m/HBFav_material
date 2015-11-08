package me.rei_m.hbfavkotlin.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark
import me.rei_m.hbfavkotlin.views.widgets.graphics.RoundedTransformation

class BookmarkLayout : RelativeLayout {

    companion object {
        private class ViewHolder {
            var description: AppCompatTextView? = null
            var title: AppCompatTextView? = null
            var iconImage: AppCompatImageView? = null
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        val holder = ViewHolder()
        holder.description = findViewById(R.id.text_bookmark_description) as AppCompatTextView
        holder.title = findViewById(R.id.text_bookmark_title) as AppCompatTextView
        holder.iconImage = findViewById(R.id.image_article_icon) as AppCompatImageView
        tag = holder
    }

    fun bindView(bookmark: Bookmark) {
        val holder = tag as ViewHolder
        if(bookmark.description.isEmpty()) {
            holder.description?.visibility = GONE
        } else {
            holder.description?.visibility = VISIBLE
            holder.description?.text = bookmark.description
        }
        holder.title?.text = bookmark.title
        Picasso.with(context)
                .load(bookmark.articleIconUrl)
                .transform(RoundedTransformation())
                .into(holder.iconImage)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        setBackgroundResource(R.drawable.bg_layout_pressed)
    }
}