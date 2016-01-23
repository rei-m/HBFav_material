package me.rei_m.hbfavmaterial.models

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.squareup.otto.Subscribe
import junit.framework.TestCase
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.repositories.MockUserErrorRepository
import me.rei_m.hbfavmaterial.repositories.MockUserRepository
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UserModelTest : TestCase() {

    lateinit private var userRepository: MockUserRepository
    lateinit private var userErrorRepository: MockUserErrorRepository

    @Before
    public override fun setUp() {
        super.setUp()
        userRepository = MockUserRepository()
        userErrorRepository = MockUserErrorRepository()
    }

    @After
    public override fun tearDown() {

    }

    @Test
    fun testCheckAndSaveUserId() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val userModel = UserModel(InstrumentationRegistry.getTargetContext(), userRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        userModel.checkAndSaveUserId(InstrumentationRegistry.getTargetContext(), "Rei19")

        // リクエスト中はBusy
        Assert.assertThat(userModel.isBusy, CoreMatchers.`is`(true))
        eventCatcher.startCountDown()

        // 正しいIDでユーザー情報が保存されている
        Assert.assertThat(eventCatcher.event.type, CoreMatchers.`is`(UserIdCheckedEvent.Companion.Type.OK))
        Assert.assertThat(userModel.isSetUserSetting(), CoreMatchers.`is`(true))
        Assert.assertThat(userModel.userEntity?.id, CoreMatchers.`is`("Rei19"))

        // ユーザーを削除する
        userModel.deleteUser(InstrumentationRegistry.getTargetContext())
        Assert.assertThat(userModel.isSetUserSetting(), CoreMatchers.`is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testCheckIdError() {
        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val userModel = UserModel(InstrumentationRegistry.getTargetContext(), userErrorRepository)

        // 削除しておく
        userModel.deleteUser(InstrumentationRegistry.getTargetContext())

        // リクエスト開始
        eventCatcher.initCountDown()
        userModel.checkAndSaveUserId(InstrumentationRegistry.getTargetContext(), "Rei19")
        eventCatcher.startCountDown()

        Assert.assertThat(eventCatcher.event.type, CoreMatchers.`is`(UserIdCheckedEvent.Companion.Type.NG))
        Assert.assertThat(userModel.isSetUserSetting(), CoreMatchers.`is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    private class EventCatcher() {

        lateinit var event: UserIdCheckedEvent

        lateinit private var countDownLatch: CountDownLatch

        fun initCountDown() {
            countDownLatch = CountDownLatch(1)
        }

        fun startCountDown() {
            countDownLatch.await(10, TimeUnit.SECONDS)
        }

        @Subscribe
        fun subscribe(e: UserIdCheckedEvent) {
            event = e
            countDownLatch.countDown()
        }
    }
}
