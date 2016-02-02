package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentFromDeveloperBinding

/**
 * 開発者からのコメントを表示するFragment.
 */
class FromDeveloperFragment : Fragment() {

    companion object {
        fun newInstance(): FromDeveloperFragment {
            return FromDeveloperFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentFromDeveloperBinding.inflate(inflater, container, false)
        binding.fragmentFromDeveloperWebView.loadUrl(getString(R.string.url_from_developer))

        return binding.root
    }
}
