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

package me.rei_m.hbfavmaterial.application

import io.reactivex.Observable
import me.rei_m.hbfavmaterial.model.entity.EditableBookmark

interface HatenaService {

    companion object {
        const val TAG_READ_AFTER = "あとで読む"
    }

    val editableBookmark: Observable<EditableBookmark>

    val registeredEvent: Observable<Unit>

    val deletedEvent: Observable<Unit>

    val unauthorizedEvent: Observable<Unit>

    val raisedErrorEvent: Observable<Unit>

    val completeFetchRequestTokenEvent: Observable<String>

    val completeRegisterAccessTokenEvent: Observable<Unit>

    val completeDeleteAccessTokenEvent: Observable<Unit>

    val confirmAuthorisedEvent: Observable<Boolean>

    val isLoading: Observable<Boolean>
    
    fun fetchRequestToken()

    fun registerAccessToken(requestToken: String)

    fun deleteAccessToken()

    fun confirmAuthorised()

    fun findBookmarkByUrl(urlString: String)

    fun registerBookmark(urlString: String,
                         comment: String,
                         isOpen: Boolean,
                         isReadAfter: Boolean,
                         tags: List<String> = listOf())

    fun deleteBookmark(urlString: String)
}
