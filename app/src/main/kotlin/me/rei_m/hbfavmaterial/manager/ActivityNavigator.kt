package me.rei_m.hbfavmaterial.manager

import android.app.Activity
import android.content.Intent
import me.rei_m.hbfavmaterial.activitiy.*
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.views.adapters.BookmarkPagerAdaptor

class ActivityNavigator {

    companion object {
        const val REQ_CODE_OAUTH = 100
    }

    fun navigateToMain(activity: Activity, page: BookmarkPagerAdaptor.Page = BookmarkPagerAdaptor.Page.BOOKMARK_FAVORITE) {
        val intentToLaunch = MainActivity.createIntent(activity, page)
        intentToLaunch.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intentToLaunch)
    }

    fun navigateToBookmark(activity: Activity, bookmarkEntity: BookmarkEntity) {
        val intentToLaunch = BookmarkActivity.createIntent(activity, bookmarkEntity)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToBookmark(activity: Activity, entryEntity: EntryEntity) {
        val intentToLaunch = BookmarkActivity.createIntent(activity, entryEntity)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToBookmarkedUsers(activity: Activity, bookmarkEntity: BookmarkEntity) {
        val intentToLaunch = BookmarkedUsersActivity.createIntent(activity, bookmarkEntity)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToExplainApp(activity: Activity) {
        val intentToLaunch = ExplainAppActivity.createIntent(activity)
        intentToLaunch.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intentToLaunch)
    }

    fun navigateToFromDeveloper(activity: Activity) {
        val intentToLaunch = FrameActivity.createIntent(activity, FrameActivity.Tag.FROM_DEVELOPER)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToCredit(activity: Activity) {
        val intentToLaunch = FrameActivity.createIntent(activity, FrameActivity.Tag.CREDIT)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToOAuth(activity: Activity) {
        val intentToLaunch = OAuthActivity.createIntent(activity)
        activity.startActivityForResult(intentToLaunch, REQ_CODE_OAUTH)
    }

    fun navigateToOthersBookmark(activity: Activity, userId: String) {
        val intentToLaunch = OthersBookmarkActivity.createIntent(activity, userId)
        activity.startActivity(intentToLaunch)
    }

    fun navigateToSetting(activity: Activity) {
        val intentToLaunch = SettingActivity.createIntent(activity)
        intentToLaunch.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intentToLaunch)
    }
}
