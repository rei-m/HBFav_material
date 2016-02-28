package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.views.widgets.list.EntryItemLayout

/**
 * エントリー一覧を管理するAdaptor.
 */
class EntryListAdapter(context: Context,
                       resource: Int) : ArrayAdapter<EntryEntity>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = convertView ?: View.inflate(context, R.layout.list_item_entry, null)

        (view as EntryItemLayout).bindView(getItem(position))

        return view
    }
}
