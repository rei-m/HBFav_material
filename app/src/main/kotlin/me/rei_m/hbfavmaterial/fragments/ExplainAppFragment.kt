package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.activities.FrameActivity
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.openUrl
import me.rei_m.hbfavmaterial.utils.AppUtil
import me.rei_m.hbfavmaterial.utils.FragmentUtil

/**
 * このアプリについてを表示するFragment.
 */
class ExplainAppFragment : BaseFragment() {

    companion object {
        fun newInstance(): ExplainAppFragment {
            return ExplainAppFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_explain_app, container, false)

        view.findViewById(R.id.fragment_explain_app_layout_review).setOnClickListener {
            getAppContext().openUrl(getString(R.string.url_review))
        }

        view.findViewById(R.id.fragment_explain_app_layout_opinion).setOnClickListener {
            getAppContext().openUrl(getString(R.string.url_opinion))
        }

        view.findViewById(R.id.fragment_explain_app_layout_from_developer).setOnClickListener {
            activity.startActivity(FrameActivity.createIntent(activity, FragmentUtil.Companion.Tag.FROM_DEVELOPER))
        }

        view.findViewById(R.id.fragment_explain_app_layout_credit).setOnClickListener {
            activity.startActivity(FrameActivity.createIntent(activity, FragmentUtil.Companion.Tag.CREDIT))
        }

        val versionName = AppUtil.getVersionName(activity.applicationContext)
        with(view.findViewById(R.id.fragment_explain_app_text_version)) {
            this as AppCompatTextView
            text = "${getString(R.string.text_version)} : $versionName"
        }

        return view
    }
}
