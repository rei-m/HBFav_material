package me.rei_m.hbfavmaterial.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import me.rei_m.hbfavmaterial.entities.BookmarkEntity

class BookmarkCountTextView : AppCompatTextView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun bindView(bookmarkEntity: BookmarkEntity) {
        text = "${bookmarkEntity.articleEntity.bookmarkCount.toString()} users"
    }
}
