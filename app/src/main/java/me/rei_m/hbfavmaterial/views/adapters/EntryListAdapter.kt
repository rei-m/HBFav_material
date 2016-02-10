package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import me.rei_m.hbfavmaterial.databinding.ListItemEntryBinding
import me.rei_m.hbfavmaterial.entities.EntryEntity

/**
 * エントリー一覧を管理するAdaptor.
 */
class EntryListAdapter(context: Context,
                       resource: Int) : ArrayAdapter<EntryEntity>(context, resource) {

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        return convertView?.apply {
            DataBindingUtil.getBinding<ListItemEntryBinding>(this).apply {
                entryEntity = getItem(position)
            }
        } ?: let {
            return ListItemEntryBinding.inflate(mLayoutInflater, parent, false).apply {
                entryEntity = getItem(position)
            }.root
        }
    }
}
