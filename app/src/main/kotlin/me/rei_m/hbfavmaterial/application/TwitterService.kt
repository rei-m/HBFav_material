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

import android.app.Activity
import android.content.Intent
import io.reactivex.Observable

interface TwitterService {

    val confirmAuthorisedEvent: Observable<Boolean>

    fun confirmAuthorised()

    fun authorize(activity: Activity)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun postTweet(articleUrl: String, articleTitle: String, comment: String)
}
