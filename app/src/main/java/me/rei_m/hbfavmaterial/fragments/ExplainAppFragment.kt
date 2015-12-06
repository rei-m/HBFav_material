package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.ClickedEvent
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.extensions.openUrl

public class ExplainAppFragment : Fragment() {

    companion object {
        public fun newInstance(): ExplainAppFragment {
            return ExplainAppFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_explain_app, container, false)

        val layoutOpinion = view.findViewById(R.id.fragment_explain_app_layout_opinion) as LinearLayout
        layoutOpinion.setOnClickListener({ v ->
            openUrl("https://github.com/rei-m/HBFav_material/issues")
        })

        val layoutFromDev = view.findViewById(R.id.fragment_explain_app_layout_from_developer) as LinearLayout
        layoutFromDev.setOnClickListener({ v ->
            EventBusHolder.EVENT_BUS.post(ClickedEvent(ClickedEvent.Companion.Type.FROM_DEVELOPER))
        })

        val layoutCredit = view.findViewById(R.id.fragment_explain_app_layout_credit) as LinearLayout
        layoutCredit.setOnClickListener({ v ->
            EventBusHolder.EVENT_BUS.post(ClickedEvent(ClickedEvent.Companion.Type.CREDIT))
        })

        return view
    }
}