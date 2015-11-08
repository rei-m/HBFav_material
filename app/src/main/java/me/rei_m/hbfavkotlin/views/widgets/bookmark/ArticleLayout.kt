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

class ArticleLayout : RelativeLayout {

    companion object {
        private class ViewHolder {
            var body: AppCompatTextView? = null
            var image: AppCompatImageView? = null
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
        tag = holder
    }

    fun bindView(bookmark: Bookmark) {
        val holder = tag as ViewHolder
        holder.body?.text = bookmark.articleBody

//        if(bookmark.description.isEmpty()) {
//            holder.description?.visibility = GONE
//        } else {
//            holder.description?.visibility = VISIBLE
//            holder.description?.text = bookmark.description
//        }
//        holder.title?.text = bookmark.title
        if(bookmark.articleImageUrl.isEmpty()) {
            holder.image?.visibility = GONE
        } else {
            holder.image?.visibility = VISIBLE
            Picasso.with(context)
                    .load(bookmark.articleImageUrl)
                    .into(holder.image)
        }
    }
}