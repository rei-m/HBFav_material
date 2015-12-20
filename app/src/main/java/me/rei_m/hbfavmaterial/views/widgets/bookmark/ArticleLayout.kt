package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.utils.BookmarkUtil

/**
 * 記事の情報を表示するレイアウト.
 */
public class ArticleLayout : RelativeLayout {

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
        tag = ViewHolder(findViewById(R.id.layout_article_text_body) as AppCompatTextView,
                findViewById(R.id.layout_article_image_body) as AppCompatImageView,
                findViewById(R.id.layout_article_text_url) as AppCompatTextView,
                findViewById(R.id.layout_article_text_add_bookmark_timing) as AppCompatTextView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            body.text = bookmarkEntity.articleEntity.body
            if (bookmarkEntity.articleEntity.bodyImageUrl.isEmpty()) {
                image.hide()
            } else {
                image.show()
                Picasso.with(context)
                        .load(bookmarkEntity.articleEntity.bodyImageUrl)
                        .into(image)
            }
            link.text = bookmarkEntity.articleEntity.url
            timing.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)
        }
    }
}
