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

import me.rei_m.hbfavmaterial.constant.EntryTypeFilter
import java.text.SimpleDateFormat
import java.util.*

class ApiUtil private constructor() {

    companion object {

        private val dateFormat: SimpleDateFormat
            get() = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        
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
