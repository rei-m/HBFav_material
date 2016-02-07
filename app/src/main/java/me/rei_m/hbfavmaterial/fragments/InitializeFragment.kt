package me.rei_m.hbfavmaterial.fragments

import android.app.ProgressDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentInitializeBinding
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.hideKeyBoard
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.models.UserModel
import rx.Subscription
import javax.inject.Inject

/**
 * アプリの初期処理を行うFragment.
 */
class InitializeFragment : Fragment(), IProgressDialog {

    @Inject
    lateinit var userModel: UserModel

    override var mProgressDialog: ProgressDialog? = null

    lateinit private var mSubscription: Subscription

    companion object {
        fun newInstance(): InitializeFragment {
            return InitializeFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.graph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentInitializeBinding.inflate(inflater, container, false)

        mSubscription = RxTextView.textChanges(binding.fragmentInitializeEditHatenaId)
                .map { v -> 0 < v.length }
                .subscribe { isEnabled -> binding.fragmentInitializeButtonSetHatenaId.isEnabled = isEnabled }

        binding.fragmentInitializeButtonSetHatenaId.setOnClickListener { v ->
            userModel.checkAndSaveUserId(getAppContext(), binding.fragmentInitializeEditHatenaId.editableText.toString())
            showProgressDialog(activity)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription.unsubscribe()
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)

        // ユーザー情報が設定済かチェックする
        if (userModel.isSetUserSetting()) {
            EventBusHolder.EVENT_BUS.post(UserIdCheckedEvent(UserIdCheckedEvent.Companion.Type.OK))
        }
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    @Subscribe
    fun subscribe(event: UserIdCheckedEvent) {

        closeProgressDialog()

        when (event.type) {
            UserIdCheckedEvent.Companion.Type.OK -> {
                // 何もしない。Activity側に処理を委ねる
            }

            UserIdCheckedEvent.Companion.Type.NG -> {
                val binding = DataBindingUtil.getBinding<FragmentInitializeBinding>(view)
                binding.fragmentInitializeLayoutHatenaId.error = getString(R.string.message_error_input_user_id)
            }

            UserIdCheckedEvent.Companion.Type.ERROR -> {
                (activity as AppCompatActivity).run {
                    hideKeyBoard(view)
                    showSnackbarNetworkError(view)
                }
            }
        }
    }
}
