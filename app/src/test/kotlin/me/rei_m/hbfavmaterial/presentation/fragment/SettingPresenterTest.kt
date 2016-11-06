package me.rei_m.hbfavmaterial.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.twitter.sdk.android.core.TwitterAuthConfig
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.presentation.activity.OAuthActivity
import me.rei_m.hbfavmaterial.presentation.manager.ActivityNavigator
import me.rei_m.hbfavmaterial.usecase.AuthorizeTwitterUsecase
import me.rei_m.hbfavmaterial.usecase.GetHatenaTokenUsecase
import me.rei_m.hbfavmaterial.usecase.GetTwitterSessionUsecase
import me.rei_m.hbfavmaterial.usecase.GetUserUsecase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers

@RunWith(MockitoJUnitRunner::class)
class SettingPresenterTest {

    @Mock
    lateinit var view: SettingContact.View

    @Mock
    lateinit var getUserUsecase: GetUserUsecase

    @Mock
    lateinit var getHatenaTokenUsecase: GetHatenaTokenUsecase

    @Mock
    lateinit var getTwitterSessionUsecase: GetTwitterSessionUsecase

    @Mock
    lateinit var authorizeTwitterUsecase: AuthorizeTwitterUsecase

    lateinit var presenter: SettingPresenter

    @Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? {
                return Schedulers.immediate()
            }
        })

        `when`(getUserUsecase.get()).thenReturn(UserEntity("hoge"))
        `when`(getHatenaTokenUsecase.get()).thenReturn(OAuthTokenEntity())
        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity())

        presenter = SettingPresenter(getUserUsecase, getHatenaTokenUsecase, getTwitterSessionUsecase, authorizeTwitterUsecase)
        presenter.onCreate(view)
        presenter.onViewCreated()
        presenter.onResume()
    }

    @After
    fun tearDown() {
        presenter.onPause()
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun testInitialize() {
        verify(view).setUserId("hoge")
        verify(view).setHatenaAuthoriseStatus(false)
        verify(view).setTwitterAuthoriseStatus(false)
    }

    @Test
    fun testOnActivityResult_twitter() {
        val mockData = mock(Intent::class.java)
        presenter.onActivityResult(TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE, AppCompatActivity.RESULT_OK, mockData)
        verify(authorizeTwitterUsecase).onActivityResult(TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE, AppCompatActivity.RESULT_OK, mockData)
    }

    @Test
    fun testOnActivityResult_hatena_success() {
        val mockData = mock(Intent::class.java)
        val mockExtra = mock(Bundle::class.java)
        `when`(mockExtra.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)).thenReturn(true)
        `when`(mockExtra.getBoolean(OAuthActivity.ARG_AUTHORIZE_STATUS)).thenReturn(true)
        `when`(mockData.extras).thenReturn(mockExtra)
        presenter.onActivityResult(ActivityNavigator.REQ_CODE_OAUTH, AppCompatActivity.RESULT_OK, mockData)
        verify(view).setHatenaAuthoriseStatus(true)
    }

    @Test
    fun testOnActivityResult_hatena_cancel() {
        val mockData = mock(Intent::class.java)
        val mockExtra = mock(Bundle::class.java)
        `when`(mockExtra.getBoolean(OAuthActivity.ARG_IS_AUTHORIZE_DONE)).thenReturn(false)
        `when`(mockData.extras).thenReturn(mockExtra)
        presenter.onActivityResult(ActivityNavigator.REQ_CODE_OAUTH, AppCompatActivity.RESULT_OK, mockData)
        verify(view).showNetworkErrorMessage()
    }

    @Test
    fun testOnClickTwitterAuthorize() {
        val mockActivity = mock(Activity::class.java)
        presenter.onClickTwitterAuthorize(mockActivity)
        verify(authorizeTwitterUsecase).authorize(mockActivity)
    }

    @Test
    fun testOnDismissEditUserIdDialog() {
        presenter.onDismissEditUserIdDialog()
        verify(view).updateUserId("hoge")
    }
}
