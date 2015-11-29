package me.rei_m.hbfavkotlin.utils

import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.entities.EntryEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Node
import java.text.SimpleDateFormat
import java.util.*

class ApiUtil private constructor() {

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        public fun parseStringToDate(dateString: String): Date {
            return dateFormat.parse(dateString)
        }
   }
}