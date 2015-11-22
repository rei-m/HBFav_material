package me.rei_m.hbfavkotlin.utils

import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Node
import java.text.SimpleDateFormat
import java.util.*

class BookmarkXmlUtils private constructor() {

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        public fun createBookmarkFromFeed(feed: Node): BookmarkEntity {

            var title = ""
            var link = ""
            var description = ""
            var creator = ""
            var date: Date? = null
            var bookmarkCount = 0
            var content = ""

            for (i_node in 0..feed.childNodes.length - 1) {
                val feedItem = feed.childNodes.item(i_node)
                when (feedItem.nodeName) {
                    "title" ->
                        title = feedItem.textContent
                    "link" ->
                        link = feedItem.textContent
                    "description" ->
                        description = feedItem.textContent
                    "dc:creator" ->
                        creator = feedItem.textContent
                    "dc:date" ->
                        date = dateFormat.parse(feedItem.textContent)
                    "hatena:bookmarkcount" ->
                        bookmarkCount = feedItem.textContent.toInt()
                    "content:encoded" ->
                        content = feedItem.textContent
                }
            }

            val parsedContent = Jsoup.parse(content)

            return BookmarkEntity(
                    title,
                    link,
                    description,
                    creator,
                    date!!,
                    bookmarkCount,
                    extractProfileIcon(parsedContent),
                    extractArticleIcon(parsedContent),
                    extractArticleBody(parsedContent),
                    extractArticleImageUrl(parsedContent))
        }

        private fun extractProfileIcon(content: Document): String = content
                .getElementsByClass("profile-image")
                .first()
                .attr("src")
                .replace("profile_s", "profile", true)

        private fun extractArticleIcon(content: Document): String = content
                .getElementsByTag("cite")
                .first()
                .getElementsByTag("img")
                .first()
                .attr("src")

        private fun extractArticleBody(content: Document): String {
            val pTags = content.getElementsByTag("p")
            val bodyIndex = pTags.size - 2
            return pTags.eq(bodyIndex - 1).text()
        }

        private fun extractArticleImageUrl(content: Document): String {
            val articleImageElement = content
                    .getElementsByClass("entry-image")
                    .first()

            return if (articleImageElement == null) "" else articleImageElement.attr("src")
        }
    }
}