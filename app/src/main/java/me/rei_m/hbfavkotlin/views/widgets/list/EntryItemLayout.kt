package me.rei_m.hbfavkotlin.views.widgets.list

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.squareup.picasso.Picasso

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.EntryEntity
import me.rei_m.hbfavkotlin.utils.RssUtils
import me.rei_m.hbfavkotlin.views.widgets.graphics.RoundedTransformation

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

        holder.title.text = entryEntity.title

        val mlp = holder.title.layoutParams as MarginLayoutParams

        if (entryEntity.articleImageUrl.isEmpty()) {
            holder.bodyImage.visibility = GONE
            mlp.rightMargin = mMarginTitleRight
        } else {
            holder.bodyImage.visibility = VISIBLE
            mlp.rightMargin = 0
            Picasso.with(context)
                    .load(entryEntity.articleImageUrl)
                    .into(holder.bodyImage)
        }

        Picasso.with(context)
                .load(entryEntity.articleIconUrl)
                .transform(RoundedTransformation())
                .into(holder.iconImage)

        holder.url.text = entryEntity.link
        holder.footer.text = entryEntity.bookmarkCount.toString() + " users - " +
                entryEntity.subject + " - " +
                RssUtils.getPastTimeString(entryEntity.date)
    }
}