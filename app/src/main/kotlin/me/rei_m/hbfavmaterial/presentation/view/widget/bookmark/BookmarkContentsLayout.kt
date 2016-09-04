package me.rei_m.hbfavmaterial.presentation.view.widget.bookmark

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity

/**
 * ブックマーク詳細のコンテンツを表示するレイアウト.
 */
class BookmarkContentsLayout : LinearLayout {

    companion object {
        private class ViewHolder(val bookmarkLayout: BookmarkLayout,
                                 val articleLayout: ArticleLayout)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        tag = ViewHolder(findViewById(R.id.layout_bookmark_contents_layout_bookmark) as BookmarkLayout,
                findViewById(R.id.layout_bookmark_contents_layout_article) as ArticleLayout)
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        val holder = tag as ViewHolder
        holder.apply {
            bookmarkLayout.bindView(bookmarkEntity)
            articleLayout.bindView(bookmarkEntity)
        }
    }
}
