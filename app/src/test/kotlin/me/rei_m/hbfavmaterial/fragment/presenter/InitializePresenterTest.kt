package me.rei_m.hbfavmaterial.fragment.presenter

import me.rei_m.hbfavmaterial.entity.UserEntity
import me.rei_m.hbfavmaterial.repository.UserRepository
import me.rei_m.hbfavmaterial.service.UserService
import me.rei_m.hbfavmaterial.testutil.TestUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class InitializePresenterTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var userService: UserService

    @Mock
    lateinit var view: InitializeContact.View

    @Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? {
                return Schedulers.immediate()
            }
        })
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun testOnCreate_initialize_not_complete_register_user() {

        `when`(userRepository.resolve()).thenReturn(UserEntity(""))

        val presenter = InitializePresenter(userRepository, userService)

        presenter.onCreate(view)
        verify(view, never()).navigateToMain()
    }

    @Test
    fun testOnCreate_initialize_complete_register_user() {

        `when`(userRepository.resolve()).thenReturn(UserEntity("test"))

        val presenter = InitializePresenter(userRepository, userService)

        presenter.onCreate(view)
        verify(view).navigateToMain()
    }

    @Test
    fun testOnClickButtonSetId_success_check_id() {

        `when`(userRepository.resolve()).thenReturn(UserEntity(""))

        doAnswer { Unit }.`when`(userRepository).store(UserEntity("success"))

        `when`(userService.confirmExistingUserId("success")).thenReturn(Observable.just(true))

        val presenter = InitializePresenter(userRepository, userService)
        presenter.onCreate(view)
        presenter.onResume()
        presenter.onClickButtonSetId("success")

        verify(userRepository, timeout(TimeUnit.SECONDS.toMillis(1).times(1))).store(UserEntity("success"))
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).navigateToMain()
    }

    @Test
    fun testOnClickButtonSetId_fail_check_id() {

        `when`(userRepository.resolve()).thenReturn(UserEntity(""))

        `when`(userService.confirmExistingUserId("fail")).thenReturn(Observable.just(false))

        val presenter = InitializePresenter(userRepository, userService)
        presenter.onCreate(view)
        presenter.onResume()
        presenter.onClickButtonSetId("fail")

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).displayInvalidUserIdMessage()
    }

    @Test
    fun testOnClickButtonSetId_fail_check_id_404() {

        `when`(userRepository.resolve()).thenReturn(UserEntity(""))

        `when`(userService.confirmExistingUserId("fail"))
                .thenReturn(Observable.error(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_NOT_FOUND)))

        val presenter = InitializePresenter(userRepository, userService)
        presenter.onCreate(view)
        presenter.onResume()
        presenter.onClickButtonSetId("fail")

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).displayInvalidUserIdMessage()
    }

    @Test
    fun testOnClickButtonSetId_fail_check_id_network_error() {

        `when`(userRepository.resolve()).thenReturn(UserEntity(""))

        `when`(userService.confirmExistingUserId("fail"))
                .thenReturn(Observable.error(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR)))

        val presenter = InitializePresenter(userRepository, userService)
        presenter.onCreate(view)
        presenter.onResume()
        presenter.onClickButtonSetId("fail")

        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
    }
}
