package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract fun setupFragmentComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFragmentComponent()
    }
}
