package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import me.rei_m.hbfavmaterial.R

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

        val view = inflater.inflate(R.layout.fragment_licence, container, false)

        with(view.findViewById(R.id.fragment_licence_web_view) as WebView) {
            loadUrl(getString(R.string.url_credit))
        }

        return view
    }
}
