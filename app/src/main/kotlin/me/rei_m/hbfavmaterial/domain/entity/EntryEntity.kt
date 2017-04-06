package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable
import java.util.*

/**
 * エントリ情報のEntity.
 */
data class EntryEntity(val article: ArticleEntity,
                       val description: String,
                       val date: Date,
                       val subject: String) : Serializable
