/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.application.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.rei_m.hbfavmaterial.application.HatenaService
import me.rei_m.hbfavmaterial.extension.subscribeAsync
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthApiService
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.infra.network.SignedRetrofitFactory
import me.rei_m.hbfavmaterial.model.entity.EditableBookmark
import me.rei_m.hbfavmaterial.model.entity.OAuthToken
import retrofit2.HttpException
import java.net.HttpURLConnection

class HatenaServiceImpl(private val preferences: SharedPreferences,
                        private val hatenaOAuthManager: HatenaOAuthManager,
                        private val signedRetrofitFactory: SignedRetrofitFactory) : HatenaService {

    companion object {

        private const val KEY_PREF_OAUTH = "KEY_PREF_OAUTH"

        private const val MAX_TAGS_COUNT = 10
    }

    private val editableBookmarkSubject = PublishSubject.create<EditableBookmark>()

    private val registeredEventSubject = PublishSubject.create<Unit>()

    private val deletedEventSubject = PublishSubject.create<Unit>()

    private val unauthorizedEventSubject = PublishSubject.create<Unit>()

    private val raisedErrorEventSubject = PublishSubject.create<Unit>()

    private val isLoadingSubject = BehaviorSubject.create<Boolean>()

    private val completeFetchRequestTokenEventSubject = PublishSubject.create<String>()
    private val completeRegisterAccessTokenEventSubject = PublishSubject.create<Unit>()
    private val completeDeleteAccessTokenEventSubject = PublishSubject.create<Unit>()
    private val confirmAuthorisedEventSubject = PublishSubject.create<Boolean>()

    override val editableBookmark: Observable<EditableBookmark> = editableBookmarkSubject

    override val registeredEvent: Observable<Unit> = registeredEventSubject

    override val deletedEvent: Observable<Unit> = deletedEventSubject

    override val unauthorizedEvent: Observable<Unit> = unauthorizedEventSubject

    override val raisedErrorEvent: Observable<Unit> = raisedErrorEventSubject

    override val completeFetchRequestTokenEvent: Observable<String> = completeFetchRequestTokenEventSubject

    override val completeRegisterAccessTokenEvent: Observable<Unit> = completeRegisterAccessTokenEventSubject

    override val completeDeleteAccessTokenEvent: Observable<Unit> = completeDeleteAccessTokenEventSubject

    override val confirmAuthorisedEvent: Observable<Boolean> = confirmAuthorisedEventSubject

    override val isLoading: Observable<Boolean> = isLoadingSubject

    override fun fetchRequestToken() {
        Single.create<String> {
            it.onSuccess(hatenaOAuthManager.retrieveRequestToken())
        }.subscribeAsync({
            if (it != null) {
                completeFetchRequestTokenEventSubject.onNext(it)
            } else {
                unauthorizedEventSubject.onNext(Unit)
            }
        }, {
            raisedErrorEventSubject.onNext(Unit)
        })
    }

    override fun registerAccessToken(requestToken: String) {
        Single.create<Boolean> {
            if (hatenaOAuthManager.retrieveAccessToken(requestToken)) {
                val token = OAuthToken(hatenaOAuthManager.consumer.token, hatenaOAuthManager.consumer.tokenSecret)
                preferences.edit()
                        .putString(KEY_PREF_OAUTH, Gson().toJson(token))
                        .apply()
                it.onSuccess(true)
            } else {
                it.onSuccess(false)
            }
        }.subscribeAsync({
            if (it) {
                completeRegisterAccessTokenEventSubject.onNext(Unit)
            } else {
                unauthorizedEventSubject.onNext(Unit)
            }
        }, {
            raisedErrorEventSubject.onNext(Unit)
        })
    }

    override fun deleteAccessToken() {
        preferences.edit().remove(KEY_PREF_OAUTH).apply()
        completeDeleteAccessTokenEventSubject.onNext(Unit)
    }

    override fun confirmAuthorised() {
        val token = getTokenFromPreferences()
        confirmAuthorisedEventSubject.onNext(token.isAuthorised)
    }

    override fun findBookmarkByUrl(urlString: String) {

        val oAuthToken = getTokenFromPreferences()

        if (!oAuthToken.isAuthorised) {
            unauthorizedEventSubject.onNext(Unit)
            return
        }

        isLoadingSubject.onNext(true)

        hatenaOAuthManager.consumer.setTokenWithSecret(oAuthToken.token, oAuthToken.secretToken)

        val retrofit = signedRetrofitFactory.create(hatenaOAuthManager.consumer)

        retrofit.create(HatenaOAuthApiService::class.java).getBookmark(urlString).map { (_, private, _, _, tags, _, comment) ->
            return@map EditableBookmark(url = urlString,
                    isFirstEdit = false,
                    comment = comment,
                    isPrivate = private,
                    tags = tags)
        }.onErrorResumeNext {
            return@onErrorResumeNext if (it is HttpException) {
                when (it.code()) {
                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        Single.just(EditableBookmark(url = urlString, isFirstEdit = true))
                    }
                    else -> {
                        Single.error(it)
                    }
                }
            } else {
                Single.error(it)
            }
        }.subscribeAsync({
            editableBookmarkSubject.onNext(it)
        }, {
            raisedErrorEventSubject.onNext(Unit)
        }, {
            isLoadingSubject.onNext(false)
        })
    }

    override fun registerBookmark(urlString: String,
                                  comment: String,
                                  isOpen: Boolean,
                                  isReadAfter: Boolean,
                                  tags: List<String>) {

        val oAuthToken = getTokenFromPreferences()

        if (!oAuthToken.isAuthorised) {
            unauthorizedEventSubject.onNext(Unit)
            return
        }

        val postTags = tags.toMutableList()
        if (isReadAfter) {
            if (!postTags.contains(HatenaService.TAG_READ_AFTER)) {
                postTags.add(HatenaService.TAG_READ_AFTER)
            }
        } else {
            if (postTags.contains(HatenaService.TAG_READ_AFTER)) {
                postTags.remove(HatenaService.TAG_READ_AFTER)
            }
        }

        require(postTags.size <= MAX_TAGS_COUNT) { "登録可能なタグは $MAX_TAGS_COUNT 個までです。" }

        isLoadingSubject.onNext(true)

        hatenaOAuthManager.consumer.setTokenWithSecret(oAuthToken.token, oAuthToken.secretToken)

        val retrofit = signedRetrofitFactory.create(hatenaOAuthManager.consumer)

        val isOpenValue = if (isOpen) "0" else "1"

        retrofit.create(HatenaOAuthApiService::class.java).postBookmark(urlString, comment, isOpenValue, postTags.toTypedArray()).subscribeAsync({
            registeredEventSubject.onNext(Unit)
        }, {
            raisedErrorEventSubject.onNext(Unit)
        }, {
            isLoadingSubject.onNext(false)
        })
    }

    override fun deleteBookmark(urlString: String) {

        val oAuthToken = getTokenFromPreferences()

        if (!oAuthToken.isAuthorised) {
            unauthorizedEventSubject.onNext(Unit)
            return
        }

        isLoadingSubject.onNext(true)

        hatenaOAuthManager.consumer.setTokenWithSecret(oAuthToken.token, oAuthToken.secretToken)

        val retrofit = signedRetrofitFactory.create(hatenaOAuthManager.consumer)

        retrofit.create(HatenaOAuthApiService::class.java).deleteBookmark(urlString).subscribeAsync({
            deletedEventSubject.onNext(Unit)
        }, {
            if (it is HttpException) {
                // 見つからなかった場合はすでに消えているので成功したとみなす.
                if (it.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    deletedEventSubject.onNext(Unit)
                    return@subscribeAsync
                }
            }
            raisedErrorEventSubject.onNext(Unit)
        }, {
            isLoadingSubject.onNext(false)
        })
    }

    private fun getTokenFromPreferences(): OAuthToken {
        val oauthJsonString = preferences.getString(KEY_PREF_OAUTH, null)
        return if (oauthJsonString != null) {
            Gson().fromJson(oauthJsonString, OAuthToken::class.java)
        } else {
            OAuthToken()
        }
    }
}