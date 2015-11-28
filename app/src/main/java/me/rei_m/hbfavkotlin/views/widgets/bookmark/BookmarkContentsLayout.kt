package me.rei_m.hbfavkotlin.views.widgets.bookmark

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import me.rei_m.hbfavkotlin.R

class BookmarkContentsLayout : LinearLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        setBackgroundResource(R.drawable.bg_layout_pressed)
    }
}
