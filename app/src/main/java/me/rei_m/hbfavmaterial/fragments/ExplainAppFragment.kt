package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.ClickedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.openUrl
import me.rei_m.hbfavmaterial.utils.AppUtil

/**
 * このアプリについてを表示するFragment.
 */
public class ExplainAppFragment : Fragment() {

    companion object {
        fun newInstance(): ExplainAppFragment {
            return ExplainAppFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_explain_app, container, false)

        val layoutOpinion = view.findViewById(R.id.fragment_explain_app_layout_opinion) as LinearLayout
        layoutOpinion.setOnClickListener { v ->
            getAppContext().openUrl(getString(R.string.url_opinion))
        }

        val layoutFromDev = view.findViewById(R.id.fragment_explain_app_layout_from_developer) as LinearLayout
        layoutFromDev.setOnClickListener { v ->
            EventBusHolder.EVENT_BUS.post(ClickedEvent(ClickedEvent.Companion.Type.FROM_DEVELOPER))
        }

        val layoutCredit = view.findViewById(R.id.fragment_explain_app_layout_credit) as LinearLayout
        layoutCredit.setOnClickListener { v ->
            EventBusHolder.EVENT_BUS.post(ClickedEvent(ClickedEvent.Companion.Type.CREDIT))
        }

        val textVersion = view.findViewById(R.id.fragment_explain_app_text_version) as AppCompatTextView
        val versionName = AppUtil.getVersionName(activity.applicationContext)
        textVersion.text = "${getString(R.string.text_version)} : $versionName"

        return view
    }
}
