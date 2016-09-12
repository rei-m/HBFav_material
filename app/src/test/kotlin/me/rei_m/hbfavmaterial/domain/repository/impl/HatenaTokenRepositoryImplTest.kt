package me.rei_m.hbfavmaterial.domain.repository.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HatenaTokenRepositoryImplTest {

    @Mock
    lateinit var mockPrefs: SharedPreferences

    @Mock
    lateinit var mockEditor: SharedPreferences.Editor

    @Test
    fun testResolve_initialize() {

        `when`(mockPrefs.getString("KEY_PREF_OAUTH", null)).thenReturn(null)

        val hatenaTokenRepository = HatenaTokenRepositoryImpl(mockPrefs)
        assertThat(hatenaTokenRepository.resolve(), `is`(OAuthTokenEntity()))
    }

    @Test
    fun testResolve_after_set() {
        `when`(mockPrefs.getString("KEY_PREF_OAUTH", null)).thenReturn(Gson().toJson(OAuthTokenEntity("token", "secretToken")))

        val hatenaTokenRepository = HatenaTokenRepositoryImpl(mockPrefs)
        assertThat(hatenaTokenRepository.resolve(), `is`(OAuthTokenEntity("token", "secretToken")))
    }

    @Test
    fun testStore() {

        `when`(mockEditor.putString("KEY_PREF_OAUTH", Gson().toJson(OAuthTokenEntity("token", "secretToken")))).thenReturn(mockEditor)

        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockPrefs.getString("KEY_PREF_OAUTH", null)).thenReturn(null)

        val hatenaTokenRepository = HatenaTokenRepositoryImpl(mockPrefs)
        hatenaTokenRepository.store(OAuthTokenEntity("token", "secretToken"))

        verify(mockEditor).apply()
        assertThat(hatenaTokenRepository.resolve(), `is`(OAuthTokenEntity("token", "secretToken")))
    }

    @Test
    fun testDelete() {

        `when`(mockEditor.remove("KEY_PREF_OAUTH")).thenReturn(mockEditor)

        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockPrefs.getString("KEY_PREF_OAUTH", null)).thenReturn(Gson().toJson(OAuthTokenEntity("token", "secretToken")))

        val hatenaTokenRepository = HatenaTokenRepositoryImpl(mockPrefs)
        hatenaTokenRepository.delete()

        verify(mockEditor).apply()
        assertThat(hatenaTokenRepository.resolve(), `is`(OAuthTokenEntity()))
    }
}
