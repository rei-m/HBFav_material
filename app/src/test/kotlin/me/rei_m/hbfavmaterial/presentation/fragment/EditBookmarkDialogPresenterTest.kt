//package me.rei_m.hbfavmaterial.presentation.fragment
//
//import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
//import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
//import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
//import me.rei_m.hbfavmaterial.domain.entity.UserEntity
//import me.rei_m.hbfavmaterial.testutil.TestUtil
//import me.rei_m.hbfavmaterial.usecase.*
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
//class EditBookmarkDialogPresenterTest {
//
//    @Mock
//    lateinit var getUserUsecase: GetUserUsecase
//
//    @Mock
//    lateinit var getTwitterSessionUsecase: GetTwitterSessionUsecase
//
//    @Mock
//    lateinit var updateUserUsecase: UpdateUserUsecase
//
//    @Mock
//    lateinit var updateTwitterSessionUsecase: UpdateTwitterSessionUsecase
//
//    @Mock
//    lateinit var registerBookmarkUsecase: RegisterBookmarkUsecase
//
//    @Mock
//    lateinit var deleteBookmarkUsecase: DeleteBookmarkUsecase
//
//    @Mock
//    lateinit var view: EditBookmarkDialogContact.View
//
//    lateinit var presenter: EditBookmarkDialogPresenter
//
//    @Before
//    fun setUp() {
//        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
//            override fun getMainThreadScheduler(): Scheduler? {
//                return Schedulers.immediate()
//            }
//        })
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity("test", true, false))
//        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity())
//
//        presenter = EditBookmarkDialogPresenter(getUserUsecase,
//                getTwitterSessionUsecase,
//                updateUserUsecase,
//                updateTwitterSessionUsecase,
//                registerBookmarkUsecase,
//                deleteBookmarkUsecase)
//    }
//
//    @After
//    fun tearDown() {
//        RxAndroidPlugins.getInstance().reset()
//    }
//
//    @Test
//    fun testOnViewCreated() {
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//
//        verify(view).setSwitchOpenCheck(true)
//        verify(view).setSwitchShareTwitterCheck(false)
//        verify(view).setSwitchReadAfterCheck(false)
//
//        `when`(getUserUsecase.get()).thenReturn(UserEntity("test", false, true))
//        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity(1, "test", OAuthTokenEntity()).apply { isShare = true })
//
//        presenter.onViewCreated()
//
//        verify(view).setSwitchOpenCheck(false)
//        verify(view).setSwitchShareTwitterCheck(true)
//        verify(view).setSwitchReadAfterCheck(true)
//    }
//
//    @Test
//    fun testOnCheckedChangeOpen() {
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//
//        presenter.onCheckedChangeOpen(true)
//        verify(updateUserUsecase).updateIsCheckedPostBookmarkOpen(true)
//        verify(view, times(2)).setSwitchOpenCheck(true)
//
//        presenter.onCheckedChangeOpen(false)
//        verify(updateUserUsecase).updateIsCheckedPostBookmarkOpen(false)
//        verify(view).setSwitchOpenCheck(false)
//    }
//
//    @Test
//    fun testOnCheckedChangeShareTwitter_authorized() {
//
//        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity().apply {
//            oAuthTokenEntity = OAuthTokenEntity(token = "token", secretToken = "secretToken")
//        })
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//
//        presenter.onCheckedChangeShareTwitter(true)
//        verify(updateTwitterSessionUsecase).updateIsShare(true)
//
//        presenter.onCheckedChangeShareTwitter(false)
//        verify(updateTwitterSessionUsecase).updateIsShare(false)
//    }
//
//    @Test
//    fun testOnCheckedChangeShareTwitter_unauthorized() {
//
//        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity().apply {
//            oAuthTokenEntity = OAuthTokenEntity(token = "", secretToken = "")
//        })
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//
//        presenter.onCheckedChangeShareTwitter(true)
//        verify(updateTwitterSessionUsecase, never()).updateIsShare(true)
//        verify(view).startSettingActivity()
//        verify(view).dismissDialog()
//
//        presenter.onCheckedChangeShareTwitter(false)
//        verify(updateTwitterSessionUsecase).updateIsShare(false)
//    }
//
//    @Test
//    fun testOnCheckedChangeReadAfter() {
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//
//        presenter.onCheckedChangeReadAfter(true)
//        verify(updateUserUsecase).updateIsCheckedPostBookmarkReadAfter(true)
//
//        presenter.onCheckedChangeReadAfter(false)
//        verify(updateUserUsecase).updateIsCheckedPostBookmarkReadAfter(false)
//    }
//
//    @Test
//    fun testOnCheckedChangeDelete() {
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//
//        presenter.onCheckedChangeDelete(true)
//        verify(view).setSwitchEnableByDelete(false)
//
//        presenter.onCheckedChangeDelete(false)
//        verify(view).setSwitchEnableByDelete(true)
//    }
//
//    @Test
//    fun testOnClickButtonOk_register_success() {
//
//        `when`(registerBookmarkUsecase.register("http://hogehoge", "bookmarkTestTitle", "fuga", arrayListOf(), true, true, true))
//                .thenReturn(Observable.just(BookmarkEditEntity("http://hogehoge", "fuga", false, arrayListOf())))
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//        presenter.onResume()
//        presenter.onClickButtonOk(false, "fuga", true, true, true)
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).dismissDialog()
//    }
//
//    @Test
//    fun testOnClickButtonOk_register_failure() {
//
//        `when`(registerBookmarkUsecase.register("http://hogehoge", "bookmarkTestTitle", "fuga", arrayListOf(), true, true, true))
//                .thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//        presenter.onResume()
//        presenter.onClickButtonOk(false, "fuga", true, true, true)
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
//    }
//
//    @Test
//    fun testOnClickButtonOk_delete_success() {
//
//        `when`(deleteBookmarkUsecase.delete("http://hogehoge")).thenReturn(Observable.just(Unit))
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//        presenter.onResume()
//        presenter.onClickButtonOk(true, "", false, false, false)
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).dismissDialog()
//    }
//
//    @Test
//    fun testOnClickButtonOk_delete_failure_notFound() {
//
//        `when`(deleteBookmarkUsecase.delete("http://hogehoge")).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_NOT_FOUND))
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//        presenter.onResume()
//        presenter.onClickButtonOk(true, "", false, false, false)
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).dismissDialog()
//    }
//
//    @Test
//    fun testOnClickButtonOk_delete_failure_error() {
//
//        `when`(deleteBookmarkUsecase.delete("http://hogehoge")).thenReturn(TestUtil.createApiErrorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR))
//
//        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
//        presenter.onViewCreated()
//        presenter.onResume()
//        presenter.onClickButtonOk(true, "", false, false, false)
//
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).hideProgress()
//        verify(view, timeout(TimeUnit.SECONDS.toMillis(1))).showNetworkErrorMessage()
//    }
//}
