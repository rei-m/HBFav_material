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

package me.rei_m.hbfavmaterial.presentation.helper

import android.app.Activity
import android.content.Intent
import me.rei_m.hbfavmaterial.model.entity.Bookmark
import me.rei_m.hbfavmaterial.model.entity.Entry
import me.rei_m.hbfavmaterial.presentation.activity.*
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter

open class Navigator(private val activity: Activity) {

    companion object {
        const val REQ_CODE_OAUTH = 100
    }

    open fun navigateToMain(page: BookmarkPagerAdapter.Page = BookmarkPagerAdapter.Page.BOOKMARK_FAVORITE) {
        val intentToLaunch = MainActivity.createIntent(activity, page)
        intentToLaunch.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intentToLaunch)
    }

    open fun navigateToBookmark(bookmark: Bookmark) {
        val intentToLaunch = BookmarkActivity.createIntent(activity, bookmark)
        activity.startActivity(intentToLaunch)
    }

    open fun navigateToBookmark(entry: Entry) {
        val intentToLaunch = BookmarkActivity.createIntent(activity, entry)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToBookmarkedUsers(bookmark: Bookmark) {
        val intentToLaunch = BookmarkedUsersActivity.createIntent(activity, bookmark)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToExplainApp() {
        val intentToLaunch = ExplainAppActivity.createIntent(activity)
        intentToLaunch.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intentToLaunch)
    }

    fun navigateToFromDeveloper() {
        val intentToLaunch = FrameActivity.createIntent(activity, FrameActivity.Tag.FROM_DEVELOPER)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToCredit() {
        val intentToLaunch = FrameActivity.createIntent(activity, FrameActivity.Tag.CREDIT)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToOAuth() {
        val intentToLaunch = OAuthActivity.createIntent(activity)
        activity.startActivityForResult(intentToLaunch, REQ_CODE_OAUTH)
    }

    open fun navigateToOthersBookmark(userId: String) {
        val intentToLaunch = OthersBookmarkActivity.createIntent(activity, userId)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToSetting() {
        val intentToLaunch = SettingActivity.createIntent(activity)
        intentToLaunch.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intentToLaunch)
    }
}
