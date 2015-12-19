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

class EntryItemLayout : RelativeLayout {

    private var mMarginTitleRight: Int = 0

    companion object {
        private class ViewHolder(val bodyImage: AppCompatImageView,
                                 val title: AppCompatTextView,
                                 val iconImage: AppCompatImageView,
                                 val url: AppCompatTextView,
                                 val footer: AppCompatTextView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(findViewById(R.id.image_article_body_image) as AppCompatImageView,
                findViewById(R.id.text_article_title) as AppCompatTextView,
                findViewById(R.id.image_article_icon) as AppCompatImageView,
                findViewById(R.id.text_article_url) as AppCompatTextView,
                findViewById(R.id.text_entry_footer) as AppCompatTextView)

        mMarginTitleRight = resources.getDimensionPixelSize(R.dimen.margin_outline)
    }

    fun bindView(entryEntity: EntryEntity) {
        val holder = tag as ViewHolder

        holder.title.text = entryEntity.articleEntity.title

        val mlp = holder.title.layoutParams as MarginLayoutParams

        if (entryEntity.articleEntity.bodyImageUrl.isEmpty()) {
            holder.bodyImage.hide()
            mlp.rightMargin = mMarginTitleRight
        } else {
            holder.bodyImage.show()
            mlp.rightMargin = 0
            Picasso.with(context)
                    .load(entryEntity.articleEntity.bodyImageUrl)
                    .into(holder.bodyImage)
        }

        Picasso.with(context)
                .load(entryEntity.articleEntity.iconUrl)
                .transform(RoundedTransformation())
                .into(holder.iconImage)

        holder.url.text = entryEntity.articleEntity.url
        holder.footer.text = entryEntity.articleEntity.bookmarkCount.toString() + " users - " +
                entryEntity.subject + " - " +
                BookmarkUtil.getPastTimeString(entryEntity.date)
    }
}