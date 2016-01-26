package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentExplainAppBinding
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.ClickedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.openUrl
import me.rei_m.hbfavmaterial.utils.AppUtil

/**
 * このアプリについてを表示するFragment.
 */
class ExplainAppFragment : Fragment() {

    companion object {
        fun newInstance(): ExplainAppFragment {
            return ExplainAppFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentExplainAppBinding.inflate(inflater, container, false)

        binding.fragmentExplainAppLayoutReview.setOnClickListener { v ->
            getAppContext().openUrl(getString(R.string.url_review))
        }

        binding.fragmentExplainAppLayoutOpinion.setOnClickListener { v ->
            getAppContext().openUrl(getString(R.string.url_opinion))
        }

        binding.fragmentExplainAppLayoutFromDeveloper.setOnClickListener { v ->
            EventBusHolder.EVENT_BUS.post(ClickedEvent(ClickedEvent.Companion.Type.FROM_DEVELOPER))
        }

        binding.fragmentExplainAppLayoutCredit.setOnClickListener { v ->
            EventBusHolder.EVENT_BUS.post(ClickedEvent(ClickedEvent.Companion.Type.CREDIT))
        }

        val versionName = AppUtil.getVersionName(activity.applicationContext)
        binding.fragmentExplainAppTextVersion.text = "${getString(R.string.text_version)} : $versionName"

        return binding.root
    }
}
