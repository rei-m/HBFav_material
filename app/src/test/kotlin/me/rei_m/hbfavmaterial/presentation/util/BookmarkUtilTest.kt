package me.rei_m.hbfavmaterial.presentation.util

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class BookmarkUtilTest {

    @Test
    fun testCreateShareText() {

        val url = "https://play.google.com/store/apps/details?id=me.rei_m.hbfavmaterial&hl=ja"
        val title = "HBFav Material はてブを流れるように見るアプリ"
        val comment = "コメントコメントコメントコメント"
        val string_5 = "12345"
        val string_120 = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"

        // コメント無しの場合 / タイトルが範囲内
        assertThat(BookmarkUtil.createShareText(url, title, ""), `is`("\"$title\" $url"))

        // コメント無しの場合 / タイトルが範囲外
        assertThat(BookmarkUtil.createShareText(url, string_120, ""), `is`("\"${string_120.take(109)}...\" $url"))

        // コメントありの場合 / コメントが範囲外, タイトルが残りの範囲内
        assertThat(BookmarkUtil.createShareText(url, string_5, string_120), `is`("${string_120.take(99)}... \"$string_5\" $url"))

        // コメントありの場合 / コメントが範囲外, タイトルが残りの範囲外
        assertThat(BookmarkUtil.createShareText(url, string_120, string_120), `is`("${string_120.take(99)}... \"${string_120.take(9)}...\" $url"))

        // コメントありの場合 / コメントが範囲内, タイトルが残りの範囲内
        assertThat(BookmarkUtil.createShareText(url, title, comment), `is`("$comment \"$title\" $url"))

        // コメントありの場合 / コメントが範囲内, タイトルが残りの範囲外
        assertThat(BookmarkUtil.createShareText(url, string_120, comment), `is`("$comment \"${string_120.take(93)}...\" $url"))
    }

    @Test
    fun testGetPastTimeString() {

        // 比較時間作成
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))

        // 現在時間作成
        val nowCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
        nowCal.set(2015, Calendar.OCTOBER, 15, 10, 0, 0)

        // 秒前
        //        cal.set(2015, Calendar.OCTOBER, 15, 9, 59, 55)
        //        assertThat(BookmarkUtil.getPastTimeString(cal.time, now.time), `is`("5秒前"))

        // 分前
        cal.set(2015, Calendar.OCTOBER, 15, 9, 57, 58)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("2分前"))

        // 時間前
        cal.set(2015, Calendar.OCTOBER, 15, 8, 59, 59)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("1時間前"))

        // 昨日
        cal.set(2015, Calendar.OCTOBER, 14, 8, 59, 59)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("昨日"))

        // 日前
        cal.set(2015, Calendar.OCTOBER, 13, 8, 59, 59)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("2日前"))

        // 先週
        cal.set(2015, Calendar.OCTOBER, 8, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("先週"))

        // 週間前
        cal.set(2015, Calendar.OCTOBER, 1, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("2週間前"))

        // 先月
        cal.set(2015, Calendar.SEPTEMBER, 8, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("先月"))

        // ヶ月前
        cal.set(2015, Calendar.AUGUST, 15, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("2ヶ月前"))

        // 昨年
        cal.set(2014, Calendar.OCTOBER, 15, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("昨年"))

        // 年前
        cal.set(2013, Calendar.OCTOBER, 15, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, nowCal), `is`("2年前"))

        // 時差がある場合
        val calAtForeign = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"))
        calAtForeign.set(2015, Calendar.OCTOBER, 15, 10, 0, 0)

        // 分前
        cal.set(2015, Calendar.OCTOBER, 15, 10, 0, 0)
        assertThat(BookmarkUtil.getPastTimeString(cal.time, calAtForeign), `is`("2時間前"))
    }
}
