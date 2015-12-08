package me.rei_m.hbfavmaterial.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.setFragment
import me.rei_m.hbfavmaterial.extensions.startActivityWithClearTop
import me.rei_m.hbfavmaterial.fragments.InitializeFragment

/**
 * 最初に起動するActivity.
 */
public class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            setFragment(InitializeFragment.newInstance())
        }
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

    /**
     * ユーザーIDチェック完了時のイベント
     */
    @Subscribe
    public fun onUserIdChecked(event: UserIdCheckedEvent) {
        if (event.type == UserIdCheckedEvent.Companion.Type.OK) {
            // IDチェックが問題なければ次の画面に進む.
            startActivityWithClearTop(MainActivity.createIntent(applicationContext))
        }
    }
}
