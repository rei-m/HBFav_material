package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R

public class SettingFragment : Fragment() {
    companion object {
        public fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        return view
    }
}