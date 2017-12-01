/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.content.Context
import android.support.v4.app.FragmentTransaction
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import me.rei_m.hbfavmaterial.R

interface MovableWithAnimation {

    var containerWidth: Float;

    fun setContainerWidth(container: ViewGroup) {
        containerWidth = container.width.toFloat()
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
