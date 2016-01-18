package me.rei_m.hbfavmaterial.models

import android.support.test.runner.AndroidJUnit4
import com.squareup.otto.Subscribe
import junit.framework.TestCase
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus
import me.rei_m.hbfavmaterial.repositories.MockBookmarkRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BookmarkFavoriteModelTest : TestCase() {

    lateinit private var bookmarkRepository: MockBookmarkRepository

    @Before
    override public fun setUp() {
        super.setUp()
        bookmarkRepository = MockBookmarkRepository()
    }

    @Test
    fun testFetchForSuccess() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val bookmarkFavoriteModel = BookmarkFavoriteModel(bookmarkRepository)

        // 最初は0件
        assertThat(bookmarkFavoriteModel.bookmarkList.size, `is`(0))

        // Busyでない
        assertThat(bookmarkFavoriteModel.isBusy, `is`(false))

        // 取得開始
        eventCatcher.initCountDown()
        bookmarkFavoriteModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS)

        // リクエスト中はBusy
        assertThat(bookmarkFavoriteModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得したあとは25件取れている
        assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.OK))
        assertThat(bookmarkFavoriteModel.bookmarkList.size, `is`(25))

        // 2ページ目を取得
        eventCatcher.initCountDown()
        bookmarkFavoriteModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS, 2)
        eventCatcher.startCountDown()

        // 取得したあとは50件取れている
        assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.OK))
        assertThat(bookmarkFavoriteModel.bookmarkList.size, `is`(50))

        // 1回目と2回目で異なるものが取れている
        assertThat(bookmarkFavoriteModel.bookmarkList[0], not(bookmarkFavoriteModel.bookmarkList[26]))

        // 先頭から取得し直す
        eventCatcher.initCountDown()
        bookmarkFavoriteModel.fetch(MockBookmarkRepository.TEST_ID_SUCCESS)
        eventCatcher.startCountDown()

        // 先頭から取得しなおした場合は今までのリストはクリアされる
        assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.OK))
        assertThat(bookmarkFavoriteModel.bookmarkList.size, `is`(25))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchForEmpty() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val bookmarkFavoriteModel = BookmarkFavoriteModel(bookmarkRepository)

        // ブックマーク登録のないユーザのRSSを取得
        eventCatcher.initCountDown()
        bookmarkFavoriteModel.fetch(MockBookmarkRepository.TEST_ID_EMPTY)
        eventCatcher.startCountDown()

        // 対象は見つからなかった
        assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.NOT_FOUND))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchForUnknown() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val bookmarkFavoriteModel = BookmarkFavoriteModel(bookmarkRepository)

        // 存在しないユーザーのRSSを取得
        eventCatcher.initCountDown()
        bookmarkFavoriteModel.fetch(MockBookmarkRepository.TEST_ID_NOT_FOUND)
        eventCatcher.startCountDown()

        // 対象は見つからなかった
        assertThat(eventCatcher.event.status, `is`(LoadedEventStatus.ERROR))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    private class EventCatcher() {

        lateinit var event: BookmarkFavoriteLoadedEvent

        lateinit private var countDownLatch: CountDownLatch

        fun initCountDown() {
            countDownLatch = CountDownLatch(1)
        }

        fun startCountDown() {
            countDownLatch.await(10, TimeUnit.SECONDS)
        }

        @Subscribe
        fun subscribe(e: BookmarkFavoriteLoadedEvent) {
            event = e
            countDownLatch.countDown()
        }
    }
}
