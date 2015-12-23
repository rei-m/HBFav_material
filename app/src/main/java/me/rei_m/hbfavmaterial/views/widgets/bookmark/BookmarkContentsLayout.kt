package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.BookmarkClickedEvent

/**
 * ブックマーク詳細のコンテンツを表示するレイアウト.
 */
public class BookmarkContentsLayout : LinearLayout {

    companion object {
        private class ViewHolder(val bookmarkLayout: BookmarkLayout,
                                 val articleLayout: ArticleLayout)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(findViewById(R.id.layout_bookmark_contents_layout_bookmark) as BookmarkLayout,
                findViewById(R.id.layout_bookmark_contents_layout_article) as ArticleLayout)
    }

    public fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            bookmarkLayout.bindView(bookmarkEntity)
            articleLayout.bindView(bookmarkEntity)
        }
        setOnClickListener {
            EventBusHolder.EVENT_BUS.post(BookmarkClickedEvent(bookmarkEntity))
        }
    }
}
