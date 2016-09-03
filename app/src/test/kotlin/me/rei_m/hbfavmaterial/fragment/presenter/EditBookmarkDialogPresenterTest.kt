package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.HatenaService
import me.rei_m.hbfavmaterial.service.TwitterService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers

@RunWith(MockitoJUnitRunner::class)
class EditBookmarkDialogPresenterTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var hatenaTokenRepository: HatenaTokenRepository

    @Mock
    lateinit var hatenaService: HatenaService

    @Mock
    lateinit var twitterSessionRepository: TwitterSessionRepository

    @Mock
    lateinit var twitterService: TwitterService

    @Mock
    lateinit var view: EditBookmarkDialogContact.View

    lateinit var presenter: EditBookmarkDialogPresenter

    @Before
    fun setUp() {
        `when`(userRepository.resolve()).thenReturn(UserEntity("test"))

        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? {
                return Schedulers.immediate()
            }
        })

        presenter = EditBookmarkDialogPresenter(userRepository, hatenaTokenRepository, hatenaService, twitterSessionRepository, twitterService)
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun testOnViewCreated() {

        `when`(twitterSessionRepository.resolve()).thenReturn(TwitterSessionEntity())

        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
        presenter.onViewCreated()

        verify(view).setSwitchOpenCheck(true)
        verify(view).setSwitchShareTwitterCheck(false)
        verify(view).setSwitchReadAfterCheck(false)

        `when`(twitterSessionRepository.resolve()).thenReturn(TwitterSessionEntity(1, "test", OAuthTokenEntity()).apply { isShare = true })
        `when`(userRepository.resolve()).thenReturn(UserEntity("test", false, true))

        presenter.onViewCreated()

        verify(view).setSwitchOpenCheck(false)
        verify(view).setSwitchShareTwitterCheck(true)
        verify(view).setSwitchReadAfterCheck(true)
    }

    @Test
    fun testOnCheckedChangeOpen() {

    }

    @Test
    fun testOnCheckedChangeShareTwitter() {

    }

    @Test
    fun testOnCheckedChangeReadAfter() {

    }

    @Test
    fun testOnCheckedChangeDelete() {

    }

    @Test
    fun testOnClickButtonOk() {

    }
}