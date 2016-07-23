package me.rei_m.hbfavmaterial.models

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.squareup.otto.Subscribe
import junit.framework.TestCase
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.network.*
import me.rei_m.hbfavmaterial.repositories.MockHatenaErrorRepository
import me.rei_m.hbfavmaterial.repositories.MockHatenaRepository
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class HatenaModelTest : TestCase() {

    lateinit private var hatenaRepository: MockHatenaRepository
    lateinit private var hatenaErrorRepository: MockHatenaErrorRepository

    @Before
    public override fun setUp() {
        super.setUp()
        hatenaRepository = MockHatenaRepository(InstrumentationRegistry.getTargetContext())
        hatenaErrorRepository = MockHatenaErrorRepository(InstrumentationRegistry.getTargetContext())
    }

    @After
    public override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun testFetchRequestToken() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.fetchRequestToken()

        // リクエスト中はBusy
        Assert.assertThat(hatenaModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得できた
        Assert.assertThat(eventCatcher.requestTokenLoadedEvent.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(eventCatcher.requestTokenLoadedEvent.authUrl, `is`(MockHatenaRepository.REQUEST_URL))

        // 取得できたらBusyではない
        Assert.assertThat(hatenaModel.isBusy, `is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchRequestTokenError() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaErrorRepository)

        // エラーの場合
        eventCatcher.initCountDown()
        hatenaModel.fetchRequestToken()
        eventCatcher.startCountDown()
        Assert.assertThat(eventCatcher.requestTokenLoadedEvent.status, `is`(LoadedEventStatus.ERROR))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchAndDeleteAccessToken() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaRepository)

        // Model作成直後の認証は通らない
        Assert.assertThat(hatenaModel.isAuthorised(), `is`(false))

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.fetchAccessToken(InstrumentationRegistry.getTargetContext(), "token")

        // リクエスト中はBusy
        Assert.assertThat(hatenaModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得できた
        Assert.assertThat(eventCatcher.accessTokenLoadedEvent.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(hatenaModel.oauthTokenEntity.token, `is`(MockHatenaRepository.TOKEN))
        Assert.assertThat(hatenaModel.oauthTokenEntity.secretToken, `is`(MockHatenaRepository.SECRET_TOKEN))

        // 取得できたらBusyではない
        Assert.assertThat(hatenaModel.isBusy, `is`(false))

        // AccessToken取得後の認証は通る
        Assert.assertThat(hatenaModel.isAuthorised(), `is`(true))

        // AccessTokenを削除
        hatenaModel.deleteAccessToken(InstrumentationRegistry.getTargetContext())
        Assert.assertThat(hatenaModel.oauthTokenEntity.token, `is`(""))
        Assert.assertThat(hatenaModel.oauthTokenEntity.secretToken, `is`(""))

        // 削除後の認証は通らない
        Assert.assertThat(hatenaModel.isAuthorised(), `is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchAccessTokenError() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaErrorRepository)

        // エラーの場合
        eventCatcher.initCountDown()
        hatenaModel.fetchAccessToken(InstrumentationRegistry.getTargetContext(), "token")
        eventCatcher.startCountDown()
        Assert.assertThat(eventCatcher.accessTokenLoadedEvent.status, `is`(LoadedEventStatus.ERROR))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchBookmark() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.fetchBookmark("bookmarkurl")

        // リクエスト中はBusy
        Assert.assertThat(hatenaModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得できた
        Assert.assertThat(eventCatcher.hatenaGetBookmarkLoadedEvent.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(eventCatcher.hatenaGetBookmarkLoadedEvent.bookmarkEditEntity, notNullValue())

        // 取得できたらBusyではない
        Assert.assertThat(hatenaModel.isBusy, `is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testFetchBookmarkError() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaErrorRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.fetchBookmark(MockHatenaErrorRepository.BOOKMARK_URL_NOT_FOUND)
        eventCatcher.startCountDown()

        // 存在しないURLだった
        Assert.assertThat(eventCatcher.hatenaGetBookmarkLoadedEvent.status, `is`(LoadedEventStatus.NOT_FOUND))
        Assert.assertThat(eventCatcher.hatenaGetBookmarkLoadedEvent.bookmarkEditEntity, nullValue())

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.fetchBookmark(MockHatenaErrorRepository.BOOKMARK_URL_ERROR)
        eventCatcher.startCountDown()

        // 例外
        Assert.assertThat(eventCatcher.hatenaGetBookmarkLoadedEvent.status, `is`(LoadedEventStatus.ERROR))
        Assert.assertThat(eventCatcher.hatenaGetBookmarkLoadedEvent.bookmarkEditEntity, nullValue())

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testRegisterBookmark() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.registerBookmark(url = "url", comment = "comment", isOpen = true, tags = listOf())

        // リクエスト中はBusy
        Assert.assertThat(hatenaModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得できた
        Assert.assertThat(eventCatcher.hatenaPostBookmarkLoadedEvent.status, `is`(LoadedEventStatus.OK))
        Assert.assertThat(eventCatcher.hatenaPostBookmarkLoadedEvent.bookmark, notNullValue())

        // 取得できたらBusyではない
        Assert.assertThat(hatenaModel.isBusy, `is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testRegisterBookmarkError() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaErrorRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.registerBookmark(url = "url", comment = "comment", isOpen = true, tags = listOf())
        eventCatcher.startCountDown()

        // ERROR
        Assert.assertThat(eventCatcher.hatenaPostBookmarkLoadedEvent.status, `is`(LoadedEventStatus.ERROR))
        Assert.assertThat(eventCatcher.hatenaPostBookmarkLoadedEvent.bookmark, nullValue())

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testDeleteBookmark() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.deleteBookmark(url = "url")

        // リクエスト中はBusy
        Assert.assertThat(hatenaModel.isBusy, `is`(true))
        eventCatcher.startCountDown()

        // 取得できた
        Assert.assertThat(eventCatcher.hatenaDeleteBookmarkLoadedEvent.status, `is`(LoadedEventStatus.OK))

        // 取得できたらBusyではない
        Assert.assertThat(hatenaModel.isBusy, `is`(false))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    @Test
    fun testDeleteBookmarkError() {

        val eventCatcher = EventCatcher()

        EventBusHolder.EVENT_BUS.register(eventCatcher)

        val hatenaModel = HatenaModel(InstrumentationRegistry.getTargetContext(), hatenaErrorRepository)

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.deleteBookmark(url = MockHatenaErrorRepository.BOOKMARK_URL_NOT_FOUND)
        eventCatcher.startCountDown()

        // 削除対象は見つからなかった
        Assert.assertThat(eventCatcher.hatenaDeleteBookmarkLoadedEvent.status, `is`(LoadedEventStatus.NOT_FOUND))

        // リクエスト開始
        eventCatcher.initCountDown()
        hatenaModel.deleteBookmark(url = MockHatenaErrorRepository.BOOKMARK_URL_ERROR)
        eventCatcher.startCountDown()

        // 例外
        Assert.assertThat(eventCatcher.hatenaDeleteBookmarkLoadedEvent.status, `is`(LoadedEventStatus.ERROR))

        EventBusHolder.EVENT_BUS.unregister(eventCatcher)
    }

    private class EventCatcher() {

        lateinit var requestTokenLoadedEvent: HatenaOAuthRequestTokenLoadedEvent
        lateinit var accessTokenLoadedEvent: HatenaOAuthAccessTokenLoadedEvent
        lateinit var hatenaGetBookmarkLoadedEvent: HatenaGetBookmarkLoadedEvent
        lateinit var hatenaPostBookmarkLoadedEvent: HatenaPostBookmarkLoadedEvent
        lateinit var hatenaDeleteBookmarkLoadedEvent: HatenaDeleteBookmarkLoadedEvent

        lateinit private var countDownLatch: CountDownLatch

        fun initCountDown() {
            countDownLatch = CountDownLatch(1)
        }

        fun startCountDown() {
            countDownLatch.await(10, TimeUnit.SECONDS)
        }

        @Subscribe
        fun subscribe(e: HatenaOAuthRequestTokenLoadedEvent) {
            requestTokenLoadedEvent = e
            countDownLatch.countDown()
        }

        @Subscribe
        fun subscribe(e: HatenaOAuthAccessTokenLoadedEvent) {
            accessTokenLoadedEvent = e
            countDownLatch.countDown()
        }

        @Subscribe
        fun subscribe(e: HatenaGetBookmarkLoadedEvent) {
            hatenaGetBookmarkLoadedEvent = e
            countDownLatch.countDown()
        }

        @Subscribe
        fun subscribe(e: HatenaPostBookmarkLoadedEvent) {
            hatenaPostBookmarkLoadedEvent = e
            countDownLatch.countDown()
        }

        @Subscribe
        fun subscribe(e: HatenaDeleteBookmarkLoadedEvent) {
            hatenaDeleteBookmarkLoadedEvent = e
            countDownLatch.countDown()
        }
    }
}
