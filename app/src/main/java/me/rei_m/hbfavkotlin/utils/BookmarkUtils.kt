package me.rei_m.hbfavkotlin.utils

import java.util.*

class BookmarkUtils private constructor() {
    companion object {
        fun getAddBookmarkTimeString(bookmarkAddedDatetime: Date): String {
            val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))

            val diffSec = cal.time.time - bookmarkAddedDatetime.time / 1000

            if(diffSec < 60) {
                return "${diffSec.toString()}秒前"
            }

            val diffMinute = diffSec / 60

            if(diffMinute < 60) {
                return "${diffMinute.toString()}分前"
            }

            val diffHour = diffMinute / 60

            if(diffHour < 24) {
                return "${diffHour.toString()}時間前"
            }

            val diffDay = diffHour / 24

            if(diffDay.toInt() == 1) {
                return "昨日"
            } else if(diffDay < 7) {
                return "${diffDay.toString()}日前"
            }

            val diffWeek = diffDay / 7

            if(diffWeek.toInt() == 1) {
                return "先週"
            } else if(diffWeek < 5) {
                return "${diffWeek.toString()}週間前"
            }

            // 先月

            // ヶ月前

            // 昨年

            // 年前

            return diffSec.toString()
        }
    }
}