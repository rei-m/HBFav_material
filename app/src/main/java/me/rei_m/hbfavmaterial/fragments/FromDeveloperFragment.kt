package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R

public class FromDeveloperFragment : Fragment() {

    companion object {
        public fun newInstance(): FromDeveloperFragment {
            return FromDeveloperFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_from_developer, container, false)

        return view
    }

}