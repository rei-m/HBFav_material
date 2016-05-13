package me.rei_m.hbfavmaterial.utils

import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.entities.EntryEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Node
import java.text.SimpleDateFormat
import java.util.*

class RssXmlUtil private constructor() {

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        fun createBookmarkFromFeed(feed: Node): BookmarkEntity {

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

            val articleEntity = ArticleEntity(
                    title = title,
                    url = link,
                    bookmarkCount = bookmarkCount,
                    iconUrl = extractArticleIcon(parsedContent),
                    body = extractArticleBodyForBookmark(parsedContent),
                    bodyImageUrl = extractArticleImageUrl(parsedContent))

            return BookmarkEntity(
                    articleEntity = articleEntity,
                    description = description,
                    creator = creator,
                    date = date!!,
                    bookmarkIconUrl = extractProfileIcon(parsedContent))
        }

        fun createEntryFromFeed(feed: Node): EntryEntity {

            var title = ""
            var link = ""
            var description = ""
            var subject = ""
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
                    "dc:subject" ->
                        subject = feedItem.textContent
                    "dc:date" ->
                        date = dateFormat.parse(feedItem.textContent)
                    "hatena:bookmarkcount" ->
                        bookmarkCount = feedItem.textContent.toInt()
                    "content:encoded" ->
                        content = feedItem.textContent
                }
            }

            val parsedContent = Jsoup.parse(content)

            val articleEntity = ArticleEntity(
                    title = title,
                    url = link,
                    bookmarkCount = bookmarkCount,
                    iconUrl = extractArticleIcon(parsedContent),
                    body = extractArticleBodyForEntry(parsedContent),
                    bodyImageUrl = extractArticleImageUrl(parsedContent))

            return EntryEntity(
                    articleEntity = articleEntity,
                    description = description,
                    date = date!!,
                    subject = subject)
        }

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
