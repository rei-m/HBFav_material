package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.HasComponent
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.openUrl
import me.rei_m.hbfavmaterial.presentation.helper.ActivityNavigator
import me.rei_m.hbfavmaterial.presentation.util.AppUtil
import javax.inject.Inject

/**
 * このアプリについてを表示するFragment.
 */
class ExplainAppFragment : BaseFragment() {

    companion object {
        fun newInstance(): ExplainAppFragment = ExplainAppFragment()
    }

    @Inject
    lateinit var navigator: ActivityNavigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_explain_app, container, false)

        view.findViewById(R.id.fragment_explain_app_layout_review).setOnClickListener {
            getAppContext().openUrl(getString(R.string.url_review))
        }

        view.findViewById(R.id.fragment_explain_app_layout_opinion).setOnClickListener {
            getAppContext().openUrl(getString(R.string.url_opinion))
        }

        view.findViewById(R.id.fragment_explain_app_layout_from_developer).setOnClickListener {
            navigator.navigateToFromDeveloper()
        }

        view.findViewById(R.id.fragment_explain_app_layout_credit).setOnClickListener {
            navigator.navigateToCredit()
        }

        val versionName = AppUtil.getVersionName(activity.applicationContext)
        with(view.findViewById(R.id.fragment_explain_app_text_version)) {
            this as AppCompatTextView
            text = "${getString(R.string.text_version)} : $versionName"
        }

        return view
    }

    @Suppress("UNCHECKED_CAST")
    override fun setupFragmentComponent() {
        (activity as HasComponent<Injector>).getComponent()
                .inject(this)
    }

    interface Injector {
        fun inject(fragment: ExplainAppFragment): ExplainAppFragment
    }
}
