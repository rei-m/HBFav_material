package me.rei_m.hbfavmaterial.models

import android.support.test.runner.AndroidJUnit4
import com.squareup.otto.Subscribe
import junit.framework.TestCase
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkUserLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.repositories.MockBookmarkRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BookmarkUserModelTest : TestCase() {

    lateinit private var bookmarkRepository: MockBookmarkRepository

    @Before
    public override fun setUp() {
        super.setUp()
        bookmarkRepository = MockBookmarkRepository()
    }

    @After
    public override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testIsSameUser() {
        val bookmarkUserModel = BookmarkUserModel(bookmarkRepository)

        // 初回は必ず同じユーザーでない
        Assert.assertThat(bookmarkUserModel.isSameUser("Rei19"), `is`(false))

        // 1回取得したら保存されることを確認
        bookmarkUserModel.fetch("Rei19")
        Assert.assertThat(bookmarkUserModel.isSameUser("Rei19"), `is`(true))
    }

    @Test
    fun testFetchForSuccess() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val bookmarkUserModel = BookmarkUserModel(bookmarkRepository)

        // 最初は0件
        Assert.assertThat(bookmarkUserModel.bookmarkList.size, `is`(0))

        // Busyでない
        Assert.assertThat(bookmarkUserModel.isBusy, `is`(false))

        // 取得開始
        eventCatcher.initCountDown()
        bookmarkUserModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS)

        // リクエスト中はBusy
        Assert.assertThat(bookmarkUserModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得したあとは25件取れている
        Assert.assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(bookmarkUserModel.bookmarkList.size, `is`(25))

        // 2ページ目を取得
        eventCatcher.initCountDown()
        bookmarkUserModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS, 2)
        eventCatcher.startCountDown()

        // 取得したあとは50件取れている
        Assert.assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(bookmarkUserModel.bookmarkList.size, `is`(50))

        // 1回目と2回目で異なるものが取れている
        Assert.assertThat(bookmarkUserModel.bookmarkList[0], not(bookmarkUserModel.bookmarkList[26]))

        // 先頭から取得し直す
        eventCatcher.initCountDown()
        bookmarkUserModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS)
        eventCatcher.startCountDown()

        // 先頭から取得しなおした場合は今までのリストはクリアされる
        Assert.assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(bookmarkUserModel.bookmarkList.size, `is`(25))

        // 取得済みの状態から他人の情報を取得する
        eventCatcher.initCountDown()
        bookmarkUserModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS_2)
        eventCatcher.startCountDown()

        // 他人の情報を取得した場合は取得した情報は破棄される
        Assert.assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.NOT_FOUND))
        Assert.assertThat(bookmarkUserModel.bookmarkList.size, `is`(0))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchForEmpty() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val bookmarkUserModel = BookmarkUserModel(bookmarkRepository)

        // ブックマーク登録のないユーザのRSSを取得
        eventCatcher.initCountDown()
        bookmarkUserModel.fetch(MockBookmarkRepository.TEST_ID_EMPTY)
        eventCatcher.startCountDown()

        // 対象は見つからなかった
        Assert.assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.NOT_FOUND))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchForUnknown() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val bookmarkUserModel = BookmarkUserModel(bookmarkRepository)

        // 存在しないユーザーのRSSを取得
        eventCatcher.initCountDown()
        bookmarkUserModel.fetch(MockBookmarkRepository.TEST_ID_NOT_FOUND)
        eventCatcher.startCountDown()

        // 対象は見つからなかった
        Assert.assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.ERROR))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    private class EventCatcher() {

        lateinit var event: BookmarkUserLoadedEvent

        lateinit private var countDownLatch: CountDownLatch

        fun initCountDown() {
            countDownLatch = CountDownLatch(1)
        }

        fun startCountDown() {
            countDownLatch.await(10, TimeUnit.SECONDS)
        }

        @Subscribe
        fun subscribe(e: BookmarkUserLoadedEvent) {
            event = e
            countDownLatch.countDown()
        }
    }
}