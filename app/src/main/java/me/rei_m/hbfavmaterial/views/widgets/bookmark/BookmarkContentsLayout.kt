package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.BookmarkClickedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder

class BookmarkContentsLayout : LinearLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    public fun bindView(bookmarkEntity: BookmarkEntity) {
        val bookmarkLayout = findViewById(R.id.layout_bookmark) as BookmarkLayout
        bookmarkLayout.bindView(bookmarkEntity)

        val articleLayout = findViewById(R.id.layout_article) as ArticleLayout
        articleLayout.bindView(bookmarkEntity)

        setOnClickListener {
            EventBusHolder.EVENT_BUS.post(BookmarkClickedEvent(bookmarkEntity))
        }
    }
}
