package me.rei_m.hbfavkotlin.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import me.rei_m.hbfavkotlin.R
import rx.Subscription

public class SplashFragment private constructor() : Fragment() {

    private var mSubscription: Subscription? = null

    companion object {
        public fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        val editIdStream = RxTextView.textChanges(view.findViewById(R.id.edit_hatena_id) as AppCompatEditText)

        val buttonSetId = view.findViewById(R.id.button_set_hatena_id) as AppCompatButton

        mSubscription = editIdStream
                .map({ v -> 0 < v.length })
                .subscribe({ isEnabled -> buttonSetId.isEnabled = isEnabled })

        buttonSetId.setOnClickListener({ v ->

        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
    }
}