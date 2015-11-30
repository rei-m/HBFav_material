package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.utils.BookmarkUtil

class ArticleLayout : RelativeLayout {

    companion object {
        private class ViewHolder(val body: AppCompatTextView,
                                 val image: AppCompatImageView,
                                 val link: AppCompatTextView,
                                 val timing: AppCompatTextView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(
                findViewById(R.id.text_article_body) as AppCompatTextView,
                findViewById(R.id.image_article_body_image) as AppCompatImageView,
                findViewById(R.id.text_article_url) as AppCompatTextView,
                findViewById(R.id.text_add_bookmark_timing) as AppCompatTextView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.body.text = bookmarkEntity.articleBody
        if (bookmarkEntity.articleImageUrl.isEmpty()) {
            holder.image.visibility = GONE
        } else {
            holder.image.visibility = VISIBLE
            Picasso.with(context)
                    .load(bookmarkEntity.articleImageUrl)
                    .into(holder.image)
        }
        holder.link.text = bookmarkEntity.link
        holder.timing.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)
    }
}