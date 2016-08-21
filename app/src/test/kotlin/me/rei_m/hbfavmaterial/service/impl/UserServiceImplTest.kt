package me.rei_m.hbfavmaterial.service.impl

import me.rei_m.hbfavmaterial.network.HatenaApiService
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner
import rx.Observable

@RunWith(MockitoJUnitRunner::class)
class UserServiceImplTest {

    @Mock
    lateinit var hatenaApiService: HatenaApiService

    @Test
    fun testConfirmExistingUserId_exist_user() {

        `when`(hatenaApiService.userCheck("success")).thenReturn(Observable.just(""))

        val userService = UserServiceImpl(hatenaApiService)
        userService.confirmExistingUserId("success").subscribe {
            assertThat(it, `is`(true))
        }
    }

    @Test
    fun testConfirmExistingUserId_not_found_user() {

        `when`(hatenaApiService.userCheck("fail")).thenReturn(Observable.just("<title>はてなブックマーク</title>"))

        val userService = UserServiceImpl(hatenaApiService)
        userService.confirmExistingUserId("fail").subscribe {
            assertThat(it, `is`(false))
        }
    }
}
