package me.rei_m.hbfavmaterial.presentation.helper

import android.app.Activity
import android.content.Intent
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.domain.entity.EntryEntity
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

    open fun navigateToBookmark(bookmarkEntity: BookmarkEntity) {
        val intentToLaunch = BookmarkActivity.createIntent(activity, bookmarkEntity)
        activity.startActivity(intentToLaunch)
    }

    open fun navigateToBookmark(entryEntity: EntryEntity) {
        val intentToLaunch = BookmarkActivity.createIntent(activity, entryEntity)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToBookmarkedUsers(bookmarkEntity: BookmarkEntity) {
        val intentToLaunch = BookmarkedUsersActivity.createIntent(activity, bookmarkEntity)
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
