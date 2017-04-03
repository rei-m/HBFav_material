package me.rei_m.hbfavmaterial.presentation.helper

import android.databinding.BindingAdapter
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.presentation.util.BookmarkUtil
import me.rei_m.hbfavmaterial.presentation.view.widget.graphics.RoundedTransformation


class BindingHelper {
    companion object {

        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String) {

            if (url.isEmpty()) return

            Picasso.with(view.context)
                    .load(url)
                    .into(view)
        }

        @JvmStatic
        @BindingAdapter("imageUrl", "isRound")
        fun loadImage(view: ImageView, url: String, isRound: Boolean) {

            if (url.isEmpty()) return

            if (isRound) {
                Picasso.with(view.context)
                        .load(url)
                        .transform(RoundedTransformation())
                        .into(view)
            } else {
                loadImage(view, url)
            }
        }

        @JvmStatic
        @BindingAdapter("onRefresh")
        fun onRefresh(view: SwipeRefreshLayout, listener: SwipeRefreshLayout.OnRefreshListener) {
            view.setOnRefreshListener(listener)
        }

        @JvmStatic
        @BindingAdapter("hasFooter")
        fun hasFooter(view: ListView, hasFooter: Boolean) {
            if (hasFooter) {
                if (0 < view.footerViewsCount) {
                    return
                }
                View.inflate(view.context, R.layout.list_fotter_loading, null).let {
                    view.addFooterView(it, null, false)
                }
            } else {
                if (view.footerViewsCount == 0) {
                    return
                }
                view.findViewById(R.layout.list_fotter_loading).let {
                    view.removeFooterView(it)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("colorSchemeResource1", "colorSchemeResource2", "colorSchemeResource3")
        fun colorSchemeResources(view: SwipeRefreshLayout, colorRes1: Int, colorRes2: Int, colorRes3: Int) {
            view.setColorSchemeColors(colorRes1, colorRes2, colorRes3)
        }

        @JvmStatic
        @BindingAdapter("userId")
        fun userId(view: NavigationView, userId: String) {

            val headerView = view.getHeaderView(0)
            val imageOwnerIcon = headerView.findViewById(R.id.nav_header_main_image_owner_icon) as ImageView

            Picasso.with(view.context)
                    .load(BookmarkUtil.getLargeIconImageUrlFromId(userId))
                    .resizeDimen(R.dimen.icon_size_nav_crop, R.dimen.icon_size_nav_crop).centerCrop()
                    .transform(RoundedTransformation())
                    .into(imageOwnerIcon)

            val textOwnerId = headerView.findViewById(R.id.nav_header_main_text_owner_name) as TextView
            textOwnerId.text = userId
        }

        @JvmStatic
        @BindingAdapter("checkedNavId")
        fun checkedNavId(view: NavigationView, @IdRes checkedNavId: Int) {
            view.setCheckedItem(checkedNavId)
        }

        @JvmStatic
        @BindingAdapter("currentItem")
        fun currentItem(view: ViewPager, currentItem: Int) {
            view.currentItem = currentItem
        }

        @JvmStatic
        @BindingAdapter("errorText")
        fun errorText(view: TextInputLayout, errorText: String) {
            view.error = errorText
        }
    }
}
