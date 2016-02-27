package me.rei_m.hbfavmaterial.models

import android.support.test.runner.AndroidJUnit4
import com.squareup.otto.Subscribe
import junit.framework.TestCase
import me.rei_m.hbfavmaterial.enums.EntryType
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.events.network.NewEntryLoadedEvent
import me.rei_m.hbfavmaterial.repositories.MockEntryErrorRepository
import me.rei_m.hbfavmaterial.repositories.MockEntryRepository
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class NewEntryModelTest : TestCase() {

    lateinit private var entryRepository: MockEntryRepository
    lateinit private var entryErrorRepository: MockEntryErrorRepository

    @Before
    public override fun setUp() {
        super.setUp()
        entryRepository = MockEntryRepository()
        entryErrorRepository = MockEntryErrorRepository()
    }

    @After
    public override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testFetch() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val newEntryModel = NewEntryModel(entryRepository)

        // 最初は0件
        Assert.assertThat(newEntryModel.entryList.size, CoreMatchers.`is`(0))

        // Busyでない
        Assert.assertThat(newEntryModel.isBusy, CoreMatchers.`is`(false))

        // 取得開始
        eventCatcher.initCountDown()
        newEntryModel.fetch(EntryType.ALL)

        // リクエスト中はBusy
        Assert.assertThat(newEntryModel.isBusy, CoreMatchers.`is`(true))
        eventCatcher.startCountDown()

        // 取得したあとは25件取れている
        Assert.assertThat(eventCatcher.event.status, CoreMatchers.`is`(LoadedEventStatus.OK))
        Assert.assertThat(newEntryModel.entryList.size, CoreMatchers.`is`(25))
        Assert.assertThat(newEntryModel.entryType, CoreMatchers.`is`(EntryType.ALL))

        // 違うカテゴリで取得し直す
        eventCatcher.initCountDown()
        newEntryModel.fetch(EntryType.ENTERTAINMENT)
        eventCatcher.startCountDown()

        Assert.assertThat(eventCatcher.event.status, CoreMatchers.`is`(LoadedEventStatus.OK))
        Assert.assertThat(newEntryModel.entryList.size, CoreMatchers.`is`(25))
        Assert.assertThat(newEntryModel.entryType, CoreMatchers.`is`(EntryType.ENTERTAINMENT))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchError() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val newEntryModel = NewEntryModel(entryErrorRepository)

        // エラーの場合
        eventCatcher.initCountDown()
        newEntryModel.fetch(EntryType.ALL)
        eventCatcher.startCountDown()
        Assert.assertThat(eventCatcher.event.status, CoreMatchers.`is`(LoadedEventStatus.ERROR))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    private class EventCatcher() {

        lateinit var event: NewEntryLoadedEvent

        lateinit private var countDownLatch: CountDownLatch

        fun initCountDown() {
            countDownLatch = CountDownLatch(1)
        }

        fun startCountDown() {
            countDownLatch.await(10, TimeUnit.SECONDS)
        }

        @Subscribe
        fun subscribe(e: NewEntryLoadedEvent) {
            event = e
            countDownLatch.countDown()
        }
    }
}