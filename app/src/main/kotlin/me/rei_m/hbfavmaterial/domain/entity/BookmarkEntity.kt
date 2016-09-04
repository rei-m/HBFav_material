package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable
import java.util.*

/**
 * ブックマーク情報のEntity.
 */
data class BookmarkEntity(val articleEntity: ArticleEntity,
                          val description: String,
                          val creator: String,
                          val date: Date,
                          val bookmarkIconUrl: String,
                          val tags: List<String> = listOf()) : Serializable
