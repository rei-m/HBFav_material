package me.rei_m.hbfavkotlin.views.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.views.widgets.list.FavoriteItemLayout

public class BookmarkAdapter constructor(context: Context, resource: Int) :
        ArrayAdapter<BookmarkEntity>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_favorite, null)

        (view as FavoriteItemLayout).bindView(getItem(position))

        return view
    }

    public val nextIndex: Int
        get() {
            return count + 1
        }
}
