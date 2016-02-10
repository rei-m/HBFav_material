package me.rei_m.hbfavmaterial.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation
import java.util.*

object DataBindingUtil {

    @JvmStatic
    fun getPastTimeString(bookmarkAddedDatetime: Date): String = BookmarkUtil.getPastTimeString(bookmarkAddedDatetime)

    @JvmStatic
    @BindingAdapter("marginRightWithLeftImage")
    fun setMarginRightWithLeftImage(view: View, imageUrl: String) {
        (view.layoutParams as ViewGroup.MarginLayoutParams).rightMargin =
                if (imageUrl.isEmpty())
                    view.resources.getDimensionPixelSize(R.dimen.margin_outline)
                else
                    0
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: AppCompatImageView, imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            Picasso.with(imageView.context)
                    .load(imageUrl)
                    .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("roundImageUrl")
    fun setRoundImageUrl(imageView: AppCompatImageView, imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            Picasso.with(imageView.context)
                    .load(imageUrl)
                    .transform(RoundedTransformation())
                    .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("userIconImageUrl")
    fun setUserIconImageUrl(imageView: AppCompatImageView, creator: String) {
        Picasso.with(imageView.context)
                .load(BookmarkUtil.getIconImageUrlFromId(creator))
                .transform(RoundedTransformation())
                .into(imageView)
    }
}
