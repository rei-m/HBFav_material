package me.rei_m.hbfavkotlin.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.utils.RssUtils

class ArticleLayout : RelativeLayout {

    companion object {
        private class ViewHolder {
            var body: AppCompatTextView? = null
            var image: AppCompatImageView? = null
            var link: AppCompatTextView? = null
            var timing: AppCompatTextView? = null
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        val holder = ViewHolder()
        holder.body = findViewById(R.id.text_article_body) as AppCompatTextView
        holder.image = findViewById(R.id.image_article_body_image) as AppCompatImageView
        holder.link = findViewById(R.id.text_article_url) as AppCompatTextView
        holder.timing = findViewById(R.id.text_add_bookmark_timing) as AppCompatTextView
        tag = holder
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.body?.text = bookmarkEntity.articleBody
        if(bookmarkEntity.articleImageUrl.isEmpty()) {
            holder.image?.visibility = GONE
        } else {
            holder.image?.visibility = VISIBLE
            Picasso.with(context)
                    .load(bookmarkEntity.articleImageUrl)
                    .into(holder.image)
        }
        holder.link?.text = bookmarkEntity.link
        holder.timing?.text = RssUtils.getPastTimeString(bookmarkEntity.date)
    }
}