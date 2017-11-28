package me.rei_m.hbfavmaterial.presentation.widget.fragment

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
        fun newInstance(): CreditFragment = CreditFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_licence, container, false)

        with(view.findViewById<WebView>(R.id.fragment_licence_web_view)) {
            loadUrl(getString(R.string.url_credit))
        }

        return view
    }
}
