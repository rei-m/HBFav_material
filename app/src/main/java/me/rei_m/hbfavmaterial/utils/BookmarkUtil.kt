package me.rei_m.hbfavmaterial.utils

import android.content.Context
import me.rei_m.hbfavmaterial.R
import java.util.*

public class BookmarkUtil private constructor() {

    companion object {

        private val COUNT_MONTHS_AT_YEAR = 12

        private val COUNT_DAYS_AT_MONTH = 30

        private val COUNT_WEEKS_AT_MONTH = 5

        private val COUNT_DAYS_AT_WEEK = 7

        private val COUNT_HOURS_AT_DAY = 24

        private val COUNT_SECONDS_AT_MINUTE = 60

        private val COUNT_MILLIS_AT_SECOND = 1000

        private val MAX_LENGTH_COMMENT_AT_TWITTER = 100

        private val MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER = 10

        private val MAX_LENGTH_TITLE_AT_TWITTER = MAX_LENGTH_COMMENT_AT_TWITTER + MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER

        private val HATENA_CDN_DOMAIN = "http://cdn1.www.st-hatena.com"

        /**
         * ブックマークのフィルタ.
         */
        enum class FilterType {
            ALL,
            COMMENT
        }

        /**
         * エントリーの種類.
         */
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

        /**
         * Twitterのシェア用のテキストを作成する.
         */
        fun createShareText(url: String, title: String, comment: String): String {
            return if (0 < comment.length) {
                val postComment: String
                val postTitle: String
                if (MAX_LENGTH_COMMENT_AT_TWITTER < comment.length) {
                    postComment = comment.take(MAX_LENGTH_COMMENT_AT_TWITTER - 1) + "..."
                    postTitle = if (MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER < (title.length)) {
                        title.take(MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER - 1) + "..."
                    } else {
                        title
                    }
                } else {
                    postComment = comment
                    val postTitleLength = MAX_LENGTH_TITLE_AT_TWITTER - comment.length
                    postTitle = if (postTitleLength < title.length) {
                        title.take(postTitleLength - 1) + "..."
                    } else {
                        title
                    }
                }
                "$postComment \"$postTitle\" $url"
            } else {
                val postTitle = if (MAX_LENGTH_TITLE_AT_TWITTER < title.length) {
                    title.substring(0, MAX_LENGTH_TITLE_AT_TWITTER - 1) + "..."
                } else {
                    title
                }
                "\"$postTitle\" $url"
            }
        }

        /**
         * フィルタに対応した文字列を取得する.
         */
        fun getFilterTypeString(context: Context, filterType: FilterType): String {

            val id = when (filterType) {
                FilterType.ALL ->
                    R.string.filter_bookmark_users_all
                FilterType.COMMENT ->
                    R.string.filter_bookmark_users_comment
            }

            return context.getString(id)
        }

        /**
         * エントリタイプに応じた文字列を取得する.
         */
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

        /**
         * ユーザーのアイコン画像のURLを取得する.
         */
        fun getIconImageUrlFromId(userId: String): String {
            return "$HATENA_CDN_DOMAIN/users/${userId.take(2)}/$userId/profile.gif"
        }

        /**
         * ユーザーのアイコン画像（大）のURLを取得する.
         */
        fun getLargeIconImageUrlFromId(userId: String): String {
            return "$HATENA_CDN_DOMAIN/users/${userId.take(2)}/$userId/user.jpg"
        }

        /**
         * 日付の差分を計算し表示用に整形する.
         */
        fun getPastTimeString(bookmarkAddedDatetime: Date,
                              cal: Date = Calendar.getInstance(TimeZone.getDefault()).time): String {

            // 時差を考慮してブックマーク追加時間と現在時間の差分を計算.
            val bookmarkCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
            bookmarkCal.time = bookmarkAddedDatetime

            val bookmarkInMills = bookmarkCal.timeInMillis - bookmarkCal.timeZone.rawOffset

            val nowCal = Calendar.getInstance(TimeZone.getDefault())
            nowCal.time = cal

            val nowTimeInMillis = nowCal.timeInMillis - nowCal.timeZone.rawOffset

            val diffSec = (nowTimeInMillis - bookmarkInMills) / COUNT_MILLIS_AT_SECOND

            if (diffSec < COUNT_SECONDS_AT_MINUTE) {
                return "${diffSec.toString()}秒前"
            }

            val diffMinute = diffSec / COUNT_SECONDS_AT_MINUTE

            if (diffMinute < COUNT_SECONDS_AT_MINUTE) {
                return "${diffMinute.toString()}分前"
            }

            val diffHour = diffMinute / COUNT_SECONDS_AT_MINUTE

            if (diffHour < COUNT_HOURS_AT_DAY) {
                return "${diffHour.toString()}時間前"
            }

            val diffDay = diffHour / COUNT_HOURS_AT_DAY

            if (diffDay.toInt() == 1) {
                return "昨日"
            } else if (diffDay < COUNT_DAYS_AT_WEEK) {
                return "${diffDay.toString()}日前"
            }

            val diffWeek = diffDay / COUNT_DAYS_AT_WEEK

            if (diffWeek.toInt() == 1) {
                return "先週"
            } else if (diffWeek < COUNT_WEEKS_AT_MONTH) {
                return "${diffWeek.toString()}週間前"
            }

            val diffMonth = diffDay / COUNT_DAYS_AT_MONTH
            if (diffMonth.toInt() == 1) {
                return "先月"
            } else if (diffMonth < COUNT_MONTHS_AT_YEAR) {
                return "${diffMonth.toString()}ヶ月前"
            }

            // 昨年
            val diffYear = diffMonth / COUNT_MONTHS_AT_YEAR
            if (diffYear.toInt() == 1) {
                return "昨年"
            } else {
                return "${diffYear.toString()}年前"
            }
        }
    }
}
