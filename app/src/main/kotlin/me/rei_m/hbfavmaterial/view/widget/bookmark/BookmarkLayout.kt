package me.rei_m.hbfavmaterial.view.widget.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entitiy.BookmarkEntity
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.show
import me.rei_m.hbfavmaterial.view.widget.graphics.RoundedTransformation

/**
 * ブックマーク情報を表示するレイアウト.
 */
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
        tag = ViewHolder(findViewById(R.id.layout_bookmark_text_description) as AppCompatTextView,
                findViewById(R.id.layout_bookmark_text_article_title) as AppCompatTextView,
                findViewById(R.id.layout_bookmark_image_article_icon) as AppCompatImageView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            if (bookmarkEntity.description.isEmpty()) {
                description.hide()
            } else {
                description.show()
                description.text = bookmarkEntity.description
            }
            title.text = bookmarkEntity.articleEntity.title
            Picasso.with(context)
                    .load(bookmarkEntity.articleEntity.iconUrl)
                    .transform(RoundedTransformation())
                    .into(iconImage)
        }
    }
}
