package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.ListItemUserBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * ユーザー一覧を管理するAdaptor.
 */
class UserListAdapter(context: Context,
                      resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        
        val view = convertView?.apply {
            val binding = DataBindingUtil.getBinding<ListItemUserBinding>(this)
            bindEntity(binding, getItem(position))
        } ?: let {
            val binding = DataBindingUtil.inflate<ListItemUserBinding>(mLayoutInflater, R.layout.list_item_user, parent, false)
            bindEntity(binding, getItem(position))
            return binding.root
        }

        return view
    }

    private fun bindEntity(binding: ListItemUserBinding, bookmarkEntity: BookmarkEntity) {

        binding.bookmarkEntity = bookmarkEntity

        // TODO XMLの中で書きたいところだけど、XML側でimportしてもstaticメソッドがいないと言われるので、いったんこっちに
        binding.listItemUserTextAddBookmarkTiming.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)

        Picasso.with(context)
                .load(BookmarkUtil.getIconImageUrlFromId(bookmarkEntity.creator))
                .transform(RoundedTransformation())
                .into(binding.listItemUserImageIcon)
    }
}
