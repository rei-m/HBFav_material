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
