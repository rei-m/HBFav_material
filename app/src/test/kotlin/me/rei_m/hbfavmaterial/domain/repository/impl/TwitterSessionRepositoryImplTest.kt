package me.rei_m.hbfavmaterial.domain.repository.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.domain.entity.OAuthTokenEntity
import me.rei_m.hbfavmaterial.domain.entity.TwitterSessionEntity
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TwitterSessionRepositoryImplTest {

    @Mock
    lateinit var mockPrefs: SharedPreferences

    @Mock
    lateinit var mockEditor: SharedPreferences.Editor

    @Test
    fun testResolve_initialize() {

        `when`(mockPrefs.getString("KEY_PREF_TWITTER_SESSION", null)).thenReturn(null)
        `when`(mockPrefs.getBoolean("KEY_PREF_IS_SHARE_TWITTER", false)).thenReturn(false)

        val twitterSessionRepository = TwitterSessionRepositoryImpl(mockPrefs)
        assertThat(twitterSessionRepository.resolve(), `is`(TwitterSessionEntity()))
    }

    @Test
    fun testResolve_after_set() {

        `when`(mockPrefs.getString("KEY_PREF_TWITTER_SESSION", null)).thenReturn(Gson().toJson(createEntity()))
        `when`(mockPrefs.getBoolean("KEY_PREF_IS_SHARE_TWITTER", false)).thenReturn(true)

        val twitterSessionRepository = TwitterSessionRepositoryImpl(mockPrefs)
        assertThat(twitterSessionRepository.resolve(), `is`(createEntity().apply { isShare = true }))
    }

    @Test
    fun testStore() {

        `when`(mockEditor.putString("KEY_PREF_TWITTER_SESSION", Gson().toJson(createEntity().apply { isShare = true }))).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean("KEY_PREF_IS_SHARE_TWITTER", true)).thenReturn(mockEditor)

        `when`(mockPrefs.edit()).thenReturn(mockEditor)

        val twitterSessionRepository = TwitterSessionRepositoryImpl(mockPrefs)
        twitterSessionRepository.store(createEntity().apply { isShare = true })

        verify(mockEditor).apply()
        assertThat(twitterSessionRepository.resolve(), `is`(createEntity().apply { isShare = true }))
    }

    @Test
    fun testDelete() {
        `when`(mockEditor.remove("KEY_PREF_TWITTER_SESSION")).thenReturn(mockEditor)
        `when`(mockEditor.remove("KEY_PREF_IS_SHARE_TWITTER")).thenReturn(mockEditor)

        `when`(mockPrefs.edit()).thenReturn(mockEditor)

        val twitterSessionRepository = TwitterSessionRepositoryImpl(mockPrefs)
        twitterSessionRepository.delete()

        verify(mockEditor).apply()
        assertThat(twitterSessionRepository.resolve(), `is`(TwitterSessionEntity()))
    }

    private fun createEntity(): TwitterSessionEntity {
        return TwitterSessionEntity(userId = 1,
                userName = "hoge",
                oAuthTokenEntity = OAuthTokenEntity(token = "token", secretToken = "secretToken"))
    }
}
