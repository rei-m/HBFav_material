package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentLicenceBinding

/**
 * アプリのクレジット表記を行うFragment.
 */
class CreditFragment : Fragment() {

    companion object {
        fun newInstance(): CreditFragment {
            return CreditFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentLicenceBinding.inflate(inflater, container, false)

        binding.fragmentLicenceWebView.loadUrl(getString(R.string.url_credit))

        return binding.root
    }
}
