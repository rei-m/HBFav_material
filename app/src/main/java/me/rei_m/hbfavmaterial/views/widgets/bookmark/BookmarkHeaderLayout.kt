package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.ui.BookmarkUserClickedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * ブックマーク詳細のヘッダを表示するレイアウト.
 */
public class BookmarkHeaderLayout : RelativeLayout {

    companion object {
        private class ViewHolder(val name: AppCompatTextView,
                                 val iconImage: AppCompatImageView)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(findViewById(R.id.layout_bookmark_header_text_user_name) as AppCompatTextView,
                findViewById(R.id.layout_bookmark_header_image_user_icon) as AppCompatImageView)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            name.text = bookmarkEntity.creator
            Picasso.with(context)
                    .load(bookmarkEntity.bookmarkIconUrl)
                    .transform(RoundedTransformation())
                    .into(iconImage)
        }
        setOnClickListener {
            EventBusHolder.EVENT_BUS.post(BookmarkUserClickedEvent(bookmarkEntity.creator))
        }
    }
}