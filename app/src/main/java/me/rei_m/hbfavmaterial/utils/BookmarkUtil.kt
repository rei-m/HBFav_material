package me.rei_m.hbfavmaterial.utils

import android.content.Context
import me.rei_m.hbfavmaterial.R
import java.util.*

public class BookmarkUtil private constructor() {

    companion object {

        enum class FilterType {
            ALL,
            COMMENT
        }

        enum class EntryType {
            ALL,
            WORLD,
            POLITICS_AND_ECONOMY,
            LIFE,
            ENTERTAINMENT,
            STUDY,
            TECHNOLOGY,
            ANIMATION_AND_GAME,
            COMEDY
        }

        fun createShareText(url: String, title: String, comment: String): String {
            val titleLength = Math.ceil(title.toByteArray().size / 3.0).toInt()

            return if (0 < comment.length) {
                val commentLength = Math.ceil(comment.toByteArray().size / 3.0).toInt()
                val postTitle = if (110 < (commentLength + titleLength)) {
                    title.substring(0, 9) + "..."
                } else {
                    title
                }
                "$comment \"$postTitle\" $url"
            } else {
                val postTitle = if (110 < titleLength) {
                    title.substring(0, 109) + "..."
                } else {
                    title
                }
                "\"$postTitle\" $url"
            }
        }

        fun getFilterTypeString(context: Context, filterType: FilterType): String {

            val id = when (filterType) {
                FilterType.ALL ->
                    R.string.filter_bookmark_users_all
                FilterType.COMMENT ->
                    R.string.filter_bookmark_users_comment
            }

            return context.getString(id)
        }

        fun getEntryTypeString(context: Context, entryType: EntryType): String {

            val id = when (entryType) {
                EntryType.ALL ->
                    R.string.category_title_all
                EntryType.WORLD ->
                    R.string.category_title_world
                EntryType.POLITICS_AND_ECONOMY ->
                    R.string.category_title_politics_and_economy
                EntryType.LIFE ->
                    R.string.category_title_life
                EntryType.ENTERTAINMENT ->
                    R.string.category_title_entertainment
                EntryType.STUDY ->
                    R.string.category_title_study
                EntryType.TECHNOLOGY ->
                    R.string.category_title_technology
                EntryType.ANIMATION_AND_GAME ->
                    R.string.category_title_animation_and_game
                EntryType.COMEDY ->
                    R.string.category_title_comedy
            }

            return context.getString(id)
        }

        fun getIconImageUrlFromId(userId: String): String {
            return "http://cdn1.www.st-hatena.com/users/${userId.take(2)}/$userId/profile.gif"
        }

        fun getLargeIconImageUrlFromId(userId: String): String {
            return "http://cdn1.www.st-hatena.com/users/${userId.take(2)}/$userId/user.jpg"
        }

        fun getPastTimeString(bookmarkAddedDatetime: Date,
                              cal: Date = Calendar.getInstance(TimeZone.getDefault()).time): String {

            val diffSec = (cal.time - bookmarkAddedDatetime.time) / 1000

            if (diffSec < 60) {
                return "${diffSec.toString()}秒前"
            }

            val diffMinute = diffSec / 60

            if (diffMinute < 60) {
                return "${diffMinute.toString()}分前"
            }

            val diffHour = diffMinute / 60

            if (diffHour < 24) {
                return "${diffHour.toString()}時間前"
            }

            val diffDay = diffHour / 24

            if (diffDay.toInt() == 1) {
                return "昨日"
            } else if (diffDay < 7) {
                return "${diffDay.toString()}日前"
            }

            val diffWeek = diffDay / 7

            if (diffWeek.toInt() == 1) {
                return "先週"
            } else if (diffWeek < 5) {
                return "${diffWeek.toString()}週間前"
            }

            val diffMonth = diffDay / 30
            if (diffMonth.toInt() == 1) {
                return "先月"
            } else if (diffMonth < 12) {
                return "${diffMonth.toString()}ヶ月前"
            }

            // 昨年
            val diffYear = diffMonth / 12
            if (diffYear.toInt() == 1) {
                return "昨年"
            } else {
                return "${diffYear.toString()}年前"
            }
        }
    }
}
