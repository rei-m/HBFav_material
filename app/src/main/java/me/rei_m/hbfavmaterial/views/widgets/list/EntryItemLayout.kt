package me.rei_m.hbfavmaterial.views.widgets.list

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.squareup.picasso.Picasso

import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * エントリー一覧のアイテムを表示するレイアウト.
 */
class EntryItemLayout : RelativeLayout {

    private var mMarginTitleRight: Int = 0

    companion object {
        private class ViewHolder(val bodyImage: AppCompatImageView,
                                 val title: AppCompatTextView,
                                 val iconImage: AppCompatImageView,
                                 val url: AppCompatTextView,
                                 val footerBookmarkCount: AppCompatTextView,
                                 val footerCategory: AppCompatTextView,
                                 val footerTiming: AppCompatTextView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(findViewById(R.id.list_item_entry_image_body) as AppCompatImageView,
                findViewById(R.id.list_item_entry_text_article_title) as AppCompatTextView,
                findViewById(R.id.list_item_entry_image_article_icon) as AppCompatImageView,
                findViewById(R.id.list_item_entry_text_article_url) as AppCompatTextView,
                findViewById(R.id.list_item_entry_text_entry_footer_user_count) as AppCompatTextView,
                findViewById(R.id.list_item_entry_text_entry_footer_category) as AppCompatTextView,
                findViewById(R.id.list_item_entry_text_entry_footer_timing) as AppCompatTextView)

        mMarginTitleRight = resources.getDimensionPixelSize(R.dimen.margin_outline)
    }

    fun bindView(entryEntity: EntryEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            title.text = entryEntity.articleEntity.title

            val mlp = holder.title.layoutParams as MarginLayoutParams
            if (entryEntity.articleEntity.bodyImageUrl.isEmpty()) {
                holder.bodyImage.hide()
                mlp.rightMargin = mMarginTitleRight
            } else {
                holder.bodyImage.show()
                mlp.rightMargin = 0
                Picasso.with(context)
                        .load(entryEntity.articleEntity.bodyImageUrl)
                        .into(bodyImage)
            }

            Picasso.with(context)
                    .load(entryEntity.articleEntity.iconUrl)
                    .transform(RoundedTransformation())
                    .into(iconImage)

            url.text = entryEntity.articleEntity.url

            val bookmarkCount = entryEntity.articleEntity.bookmarkCount.toString()
            val pastTimeString = BookmarkUtil.getPastTimeString(entryEntity.date)
            
            footerBookmarkCount.text = "$bookmarkCount users"
            footerCategory.text = " - ${entryEntity.subject} - "
            footerTiming.text = pastTimeString
        }
    }
}
