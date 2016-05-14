package me.rei_m.hbfavmaterial.utils

import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*

class RssXmlUtil private constructor() {

    companion object {

        private val dateFormat: SimpleDateFormat
            get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        fun parseStringToDate(dateString: String): Date {
            return dateFormat.parse(dateString)
        }

        fun extractProfileIcon(content: Document): String = content
                .getElementsByClass("profile-image")
                .first()
                .attr("src")
                .replace("profile_s", "profile", true)

        fun extractArticleIcon(content: Document): String = content
                .getElementsByTag("cite")
                .first()
                .getElementsByTag("img")
                .first()
                .attr("src")

        fun extractArticleBodyForBookmark(content: Document): String {
            val pTags = content.getElementsByTag("p")
            val bodyIndex = pTags.size - 3
            return pTags.eq(bodyIndex).text()
        }

        fun extractArticleBodyForEntry(content: Document): String {
            val pTags = content.getElementsByTag("p")
            val bodyIndex = pTags.size - 2
            return pTags.eq(bodyIndex).text()
        }

        fun extractArticleImageUrl(content: Document): String {
            val articleImageElement = content
                    .getElementsByClass("entry-image")
                    .first()

            return if (articleImageElement == null) "" else articleImageElement.attr("src")
        }
    }
}
