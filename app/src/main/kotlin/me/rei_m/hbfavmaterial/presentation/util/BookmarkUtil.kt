package me.rei_m.hbfavmaterial.presentation.util

import java.util.*

class BookmarkUtil private constructor() {

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
         * Twitterのシェア用のテキストを作成する.
         */
        fun createShareText(url: String, title: String, comment: String): String {
            return if (comment.isNotEmpty()) {
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
         * ユーザーのアイコン画像のURLを取得する.
         */
        @JvmStatic
        fun getIconImageUrlFromId(userId: String): String {
            return "$HATENA_CDN_DOMAIN/users/${userId.take(2)}/$userId/profile.gif"
        }

        /**
         * ユーザーのアイコン画像（大）のURLを取得する.
         */
        @JvmStatic
        fun getLargeIconImageUrlFromId(userId: String): String {
            return "$HATENA_CDN_DOMAIN/users/${userId.take(2)}/$userId/user.jpg"
        }

        /**
         * 日付の差分を計算し表示用に整形する.
         */
        @JvmStatic
        fun getPastTimeString(bookmarkAddedDatetime: Date,
                              nowCalendar: Calendar = Calendar.getInstance(TimeZone.getDefault())): String {

            // 時差を考慮してブックマーク追加時間と現在時間の差分を計算.
            val bookmarkCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
            bookmarkCal.time = bookmarkAddedDatetime

            val nowInMills = nowCalendar.timeInMillis - nowCalendar.timeZone.rawOffset
            val bookmarkInMills = bookmarkCal.timeInMillis - bookmarkCal.timeZone.rawOffset

            val diffSec = (nowInMills - bookmarkInMills) / COUNT_MILLIS_AT_SECOND

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

        @JvmStatic
        fun getPastTimeString(bookmarkAddedDatetime: Date): String {
            return getPastTimeString(bookmarkAddedDatetime,
                    Calendar.getInstance(TimeZone.getDefault()))
        }
    }
}
