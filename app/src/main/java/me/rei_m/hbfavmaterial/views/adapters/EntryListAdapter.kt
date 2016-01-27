package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ListItemEntryBinding
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.extensions.hide
import me.rei_m.hbfavmaterial.extensions.show
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * エントリー一覧を管理するAdaptor.
 */
class EntryListAdapter(context: Context,
                       resource: Int) : ArrayAdapter<EntryEntity>(context, resource) {

    private val mLayoutInflater = LayoutInflater.from(context)

    private val mMarginTitleRight: Int = context.resources.getDimensionPixelSize(R.dimen.margin_outline)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        return convertView?.apply {
            val binding = DataBindingUtil.getBinding<ListItemEntryBinding>(this)
            bindEntity(binding, getItem(position))
        } ?: let {
            val binding = ListItemEntryBinding.inflate(mLayoutInflater, parent, false)
            bindEntity(binding, getItem(position))
            return binding.root
        }
    }

    private fun bindEntity(binding: ListItemEntryBinding, entryEntity: EntryEntity) {

        binding.entryEntity = entryEntity

        val mlp = binding.listItemEntryTextArticleTitle.layoutParams as ViewGroup.MarginLayoutParams
        if (entryEntity.articleEntity.bodyImageUrl.isEmpty()) {
            binding.listItemEntryImageBody.hide()
            mlp.rightMargin = mMarginTitleRight
        } else {
            binding.listItemEntryImageBody.show()
            mlp.rightMargin = 0
            Picasso.with(context)
                    .load(entryEntity.articleEntity.bodyImageUrl)
                    .into(binding.listItemEntryImageBody)
        }

        Picasso.with(context)
                .load(entryEntity.articleEntity.iconUrl)
                .transform(RoundedTransformation())
                .into(binding.listItemEntryImageArticleIcon)

        binding.listItemEntryTextEntryFooterTiming.text = BookmarkUtil.getPastTimeString(entryEntity.date)
    }
}
