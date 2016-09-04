package me.rei_m.hbfavmaterial.domain.repository.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import me.rei_m.hbfavmaterial.domain.entity.UserEntity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserRepositoryImplTest {

    @Mock
    lateinit var mockContext: Context

    @Mock
    lateinit var mockPrefs: SharedPreferences

    @Mock
    lateinit var mockEditor: SharedPreferences.Editor

    @Test
    fun testResolve_initialize() {

        `when`(mockPrefs.getString("KEY_PREF_USER", null)).thenReturn(null)
        `when`(mockContext.getSharedPreferences("UserModel", Context.MODE_PRIVATE)).thenReturn(mockPrefs)

        val userRepository = UserRepositoryImpl(mockContext)
        assertThat(userRepository.resolve(), `is`(UserEntity("")))
    }

    @Test
    fun testResolve_after_set_account() {

        `when`(mockPrefs.getString("KEY_PREF_USER", null)).thenReturn(Gson().toJson(UserEntity("test")))
        `when`(mockContext.getSharedPreferences("UserModel", Context.MODE_PRIVATE)).thenReturn(mockPrefs)

        val userRepository = UserRepositoryImpl(mockContext)
        assertThat(userRepository.resolve(), `is`(UserEntity("test")))
    }

    @Test
    fun testStore() {

        `when`(mockEditor.putString("KEY_PREF_USER", Gson().toJson(UserEntity("test")))).thenReturn(mockEditor)

        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockPrefs.getString("KEY_PREF_USER", null)).thenReturn(null)

        `when`(mockContext.getSharedPreferences("UserModel", Context.MODE_PRIVATE)).thenReturn(mockPrefs)

        val userRepository = UserRepositoryImpl(mockContext)
        userRepository.store(UserEntity("test"))

        verify(mockEditor).apply()
        assertThat(userRepository.resolve(), `is`(UserEntity("test")))
    }

    @Test
    fun testDelete() {

        `when`(mockEditor.remove("KEY_PREF_USER")).thenReturn(mockEditor)

        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockPrefs.getString("KEY_PREF_USER", null)).thenReturn(Gson().toJson(UserEntity("test")))

        `when`(mockContext.getSharedPreferences("UserModel", Context.MODE_PRIVATE)).thenReturn(mockPrefs)

        val userRepository = UserRepositoryImpl(mockContext)

        userRepository.delete()

        verify(mockEditor).apply()
        assertThat(userRepository.resolve(), `is`(UserEntity("")))
    }
}