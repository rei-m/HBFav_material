//package me.rei_m.hbfavmaterial.presentation.fragment
//
//import me.rei_m.hbfavmaterial.domain.entity.UserEntity
//import me.rei_m.hbfavmaterial.testutil.TestUtil
//import me.rei_m.hbfavmaterial.usecase.ConfirmExistingUserIdUsecase
//import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.runners.MockitoJUnitRunner
//import rx.Observable
//import rx.Scheduler
//import rx.android.plugins.RxAndroidPlugins
//import rx.android.plugins.RxAndroidSchedulersHook
//import rx.schedulers.Schedulers
//import java.net.HttpURLConnection
//import java.util.concurrent.TimeUnit
//
//@RunWith(MockitoJUnitRunner::class)
//class InitializePresenterTest {
//
//    @Mock
//    lateinit var getUserUsecase: GetUserUsecase
//
//    @Mock
//    lateinit var confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase
//
//    @Mock
//    lateinit var view: InitializeContact.View
//
//    @Before
//    fun setUp() {
//        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
//            override fun getMainThreadScheduler(): Scheduler? {
//                return Schedulers.immediate()
//            }
//        })
//    }
//
//    @After
//    fun tearDown() {
//        RxAndroidPlugins.getInstance().reset()
//    }
//
//    @Test
//    fun testOnCreate_initialize_not_complete_register_user() {
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity(""))
//
//        val presenter = InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
//
//        presenter.onCreate(view)
//        verify(view, never()).navigateToMain()
//    }
//
//    @Test
//    fun testOnCreate_initialize_complete_register_user() {
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity("test"))
//
//        val presenter = InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
//
//        presenter.onCreate(view)
//        verify(view).navigateToMain()
//    }
//
//    @Test
//    fun testOnClickButtonSetId_success_check_id() {
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity(""))
//
//        `when`(confirmExistingUserIdUsecase.confirm("success")).thenReturn(Observable.just(true))
//
//        val presenter = InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
//        presenter.onCreate(view)
//        presenter.onResume()
//        presenter.onClickButtonSetId("success")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).navigateToMain()
//    }
//
//    @Test
//    fun testOnClickButtonSetId_fail_check_id() {
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity(""))
//
//        `when`(confirmExistingUserIdUsecase.confirm("fail")).thenReturn(Observable.just(false))
//
//        val presenter = InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
//        presenter.onCreate(view)
//        presenter.onResume()
//        presenter.onClickButtonSetId("fail")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).displayInvalidUserIdMessage()
//    }
//
//    @Test
//    fun testOnClickButtonSetId_fail_check_id_404() {
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity(""))
//
//        `when`(confirmExistingUserIdUsecase.confirm("fail"))
//                .thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_NOT_FOUND))
//
//        val presenter = InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
//        presenter.onCreate(view)
//        presenter.onResume()
//        presenter.onClickButtonSetId("fail")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).displayInvalidUserIdMessage()
//    }
//
//    @Test
//    fun testOnClickButtonSetId_fail_check_id_network_error() {
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity(""))
//
//        `when`(confirmExistingUserIdUsecase.confirm("fail"))
//                .thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))
//
//        val presenter = InitializePresenter(getUserUsecase, confirmExistingUserIdUsecase)
//        presenter.onCreate(view)
//        presenter.onResume()
//        presenter.onClickButtonSetId("fail")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
//    }
//}
