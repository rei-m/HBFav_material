package me.rei_m.hbfavkotlin.views.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark
import me.rei_m.hbfavkotlin.views.widgets.list.BookmarkItemLayout

public class BookmarkAdaptor constructor(context: Context, resource: Int) :
        ArrayAdapter<Bookmark>(context, resource){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_bookmark, null)

        (view as BookmarkItemLayout).bindView(getItem(position))

        return view
    }

    public val nextIndex: Int
        get() {
            return count + 1
        }
}
