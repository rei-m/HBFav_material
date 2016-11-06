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
//class EditUserIdDialogPresenterTest {
//
//    @Mock
//    lateinit var getUserUsecase: GetUserUsecase
//
//    @Mock
//    lateinit var confirmExistingUserIdUsecase: ConfirmExistingUserIdUsecase
//
//    @Mock
//    lateinit var view: EditUserIdDialogContact.View
//
//    lateinit var presenter: EditUserIdDialogPresenter
//
//    @Before
//    fun setUp() {
//        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
//            override fun getMainThreadScheduler(): Scheduler? {
//                return Schedulers.immediate()
//            }
//        })
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity("hoge"))
//
//        presenter = EditUserIdDialogPresenter(getUserUsecase, confirmExistingUserIdUsecase)
//        presenter.onCreate(view)
//        presenter.onViewCreated()
//        presenter.onResume()
//    }
//
//    @After
//    fun tearDown() {
//        presenter.onPause()
//        RxAndroidPlugins.getInstance().reset()
//    }
//
//    @Test
//    fun testInitialize() {
//        verify(view).setEditUserId("hoge")
//    }
//
//    @Test
//    fun testOnClickButtonOk_sameId() {
//
//        `when`(confirmExistingUserIdUsecase.confirm("hoge")).thenReturn(Observable.just(true))
//
//        presenter.onClickButtonOk("hoge")
//
//        verify(confirmExistingUserIdUsecase, never()).confirm("hoge")
//        verify(view).dismissDialog()
//    }
//
//    @Test
//    fun testOnClickButtonOk_success() {
//
//        `when`(confirmExistingUserIdUsecase.confirm("fuga")).thenReturn(Observable.just(true))
//
//        presenter.onClickButtonOk("fuga")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).dismissDialog()
//    }
//
//    @Test
//    fun testOnClickButtonOk_success_invalidId() {
//
//        `when`(confirmExistingUserIdUsecase.confirm("fuga")).thenReturn(Observable.just(false))
//
//        presenter.onClickButtonOk("fuga")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).displayInvalidUserIdMessage()
//    }
//
//    @Test
//    fun testOnClickButtonOk_failure_notFound() {
//
//        `when`(confirmExistingUserIdUsecase.confirm("fuga")).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_NOT_FOUND))
//
//        presenter.onClickButtonOk("fuga")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).displayInvalidUserIdMessage()
//    }
//
//    @Test
//    fun testOnClickButtonOk_failure_error() {
//
//        `when`(confirmExistingUserIdUsecase.confirm("fuga")).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))
//
//        presenter.onClickButtonOk("fuga")
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
//    }
//}
