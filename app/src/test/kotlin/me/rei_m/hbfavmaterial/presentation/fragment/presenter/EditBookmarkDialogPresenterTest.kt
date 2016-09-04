package me.rei_m.hbfavmaterial.presentation.fragment.presenter

import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogContact
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogPresenter
import me.rei_m.hbfavmaterial.usecase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.schedulers.Schedulers

@RunWith(MockitoJUnitRunner::class)
class EditBookmarkDialogPresenterTest {

    @Mock
    lateinit var getUserUsecase: GetUserUsecase

    @Mock
    lateinit var getTwitterSessionUsecase: GetTwitterSessionUsecase

    @Mock
    lateinit var updateUserUsecase: UpdateUserUsecase

    @Mock
    lateinit var updateTwitterSessionUsecase: UpdateTwitterSessionUsecase

    @Mock
    lateinit var registerBookmarkUsecase: RegisterBookmarkUsecase

    @Mock
    lateinit var deleteBookmarkUsecase: DeleteBookmarkUsecase

    @Mock
    lateinit var view: EditBookmarkDialogContact.View

    lateinit var presenter: EditBookmarkDialogPresenter

    @Before
    fun setUp() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler? {
                return Schedulers.immediate()
            }
        })

        presenter = EditBookmarkDialogPresenter(getUserUsecase,
                getTwitterSessionUsecase,
                updateUserUsecase,
                updateTwitterSessionUsecase,
                registerBookmarkUsecase,
                deleteBookmarkUsecase)
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.getInstance().reset()
    }

    @Test
    fun testOnViewCreated() {

        `when`(getUserUsecase.get()).thenReturn(UserEntity("test", true, false))
        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity())

        presenter.onCreate(view, "http://hogehoge", "bookmarkTestTitle", null)
        presenter.onViewCreated()

        verify(view).setSwitchOpenCheck(true)
        verify(view).setSwitchShareTwitterCheck(false)
        verify(view).setSwitchReadAfterCheck(false)

        `when`(getTwitterSessionUsecase.get()).thenReturn(TwitterSessionEntity(1, "test", OAuthTokenEntity()).apply { isShare = true })
        `when`(getUserUsecase.get()).thenReturn(UserEntity("test", false, true))

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