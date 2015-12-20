package me.rei_m.hbfavmaterial.utils

import me.rei_m.hbfavmaterial.utils.BookmarkUtil.Companion.EntryType
import java.text.SimpleDateFormat
import java.util.*

public class ApiUtil private constructor() {

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        public fun parseStringToDate(dateString: String): Date {
            return dateFormat.parse(dateString)
        }

        public fun getEntryTypeRss(entryType: EntryType): String = when (entryType) {
            EntryType.WORLD ->
                "social.rss"
            EntryType.POLITICS_AND_ECONOMY ->
                "economics.rss"
            EntryType.LIFE ->
                "life.rss"
            EntryType.ENTERTAINMENT ->
                "entertainment.rss"
            EntryType.STUDY ->
                "knowledge.rss"
            EntryType.TECHNOLOGY ->
                "it.rss"
            EntryType.ANIMATION_AND_GAME ->
                "game.rss"
            EntryType.COMEDY ->
                "fun.rss"
            else ->
                ""
        }
    }
}
