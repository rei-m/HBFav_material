package me.rei_m.hbfavmaterial.fragments

import android.content.Context
import android.support.v4.app.FragmentTransaction
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import me.rei_m.hbfavmaterial.R

interface FragmentAnimationI {

    var mContainerWidth: Float;

    fun setContainerWidth(container: ViewGroup) {
        mContainerWidth = container.width.toFloat()
    }

    fun createAnimatorMoveSlide(transit: Int, enter: Boolean, nextAnim: Int, context: Context): Animation? {
        // FragmentTransactionにTRANSIT_FRAGMENT_OPENを指定しておくと、
        // 遷移時にはTRANSIT_FRAGMENT_OPEN、Back時にはTRANSIT_FRAGMENT_CLOSEが渡される
        return when (transit) {
            FragmentTransaction.TRANSIT_FRAGMENT_OPEN ->
                AnimationUtils.loadAnimation(context, if (enter) R.anim.slide_in_left else R.anim.slide_out_left)
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE ->
                AnimationUtils.loadAnimation(context, if (enter) R.anim.slide_in_right else R.anim.slide_out_right)
            else ->
                null
        }
    }
}
