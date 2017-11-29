package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import me.rei_m.hbfavmaterial.R

/**
 * 開発者からのコメントを表示するFragment.
 */
class FromDeveloperFragment : Fragment() {

    companion object {
        fun newInstance(): FromDeveloperFragment = FromDeveloperFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_from_developer, container, false)

        with(view.findViewById<View>(R.id.fragment_from_developer_web_view)) {
            this as WebView
            loadUrl(getString(R.string.url_from_developer))
        }

        return view
    }
}
