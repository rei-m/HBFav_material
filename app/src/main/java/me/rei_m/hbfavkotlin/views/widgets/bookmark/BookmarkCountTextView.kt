package me.rei_m.hbfavkotlin.views.widgets.bookmark

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.BookmarkCountClickedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder

class BookmarkCountTextView : AppCompatTextView {constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    public fun bindView(bookmarkEntity: BookmarkEntity) {
        text = bookmarkEntity.bookmarkCount.toString() + " users"

        setOnClickListener({
            EventBusHolder.EVENT_BUS.post(BookmarkCountClickedEvent(bookmarkEntity))
        })
    }
}