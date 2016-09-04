package me.rei_m.hbfavmaterial.domain.util

import me.rei_m.hbfavmaterial.enum.EntryTypeFilter
import java.text.SimpleDateFormat
import java.util.*

class ApiUtil private constructor() {

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        fun parseStringToDate(dateString: String): Date {
            return dateFormat.parse(dateString)
        }

        fun getEntryTypeRss(entryTypeFilter: EntryTypeFilter): String = when (entryTypeFilter) {
            EntryTypeFilter.WORLD ->
                "social.rss"
            EntryTypeFilter.POLITICS_AND_ECONOMY ->
                "economics.rss"
            EntryTypeFilter.LIFE ->
                "life.rss"
            EntryTypeFilter.ENTERTAINMENT ->
                "entertainment.rss"
            EntryTypeFilter.STUDY ->
                "knowledge.rss"
            EntryTypeFilter.TECHNOLOGY ->
                "it.rss"
            EntryTypeFilter.ANIMATION_AND_GAME ->
                "game.rss"
            EntryTypeFilter.COMEDY ->
                "fun.rss"
            else ->
                ""
        }
    }
}
