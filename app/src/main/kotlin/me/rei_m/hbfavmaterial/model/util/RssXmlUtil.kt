/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.model.util

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
