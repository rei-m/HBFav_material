package me.rei_m.hbfavkotlin.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.events.UserIdCheckedEvent
import me.rei_m.hbfavkotlin.extensions.getAppContext
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.UserModel
import rx.Subscription

public class SplashFragment private constructor() : Fragment(), ProgressDialogI {

    override var mProgressDialog: ProgressDialog? = null

    private var mSubscription: Subscription? = null

    companion object {
        public fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // IDチェック
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        val editId = view.findViewById(R.id.edit_hatena_id) as AppCompatEditText
        val editIdStream = RxTextView.textChanges(editId)
        val buttonSetId = view.findViewById(R.id.button_set_hatena_id) as AppCompatButton

        mSubscription = editIdStream
                .map({ v -> 0 < v.length })
                .subscribe({ isEnabled -> buttonSetId.isEnabled = isEnabled })

        buttonSetId.setOnClickListener({ v ->
            val userModel = ModelLocator.get(ModelLocator.Companion.Tag.USER) as UserModel
            userModel.checkAndSaveUserId(getAppContext(), editId.editableText.toString())
            showProgressDialog(activity)
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onUserIdChecked(event: UserIdCheckedEvent) {

        closeProgressDialog()

        when (event.type) {
            UserIdCheckedEvent.Companion.Type.OK -> {
                // 何もしない。Activity側に処理を委ねる
            }

            UserIdCheckedEvent.Companion.Type.NG -> {
                // TODO エラー表示
            }

            UserIdCheckedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }
    }
}