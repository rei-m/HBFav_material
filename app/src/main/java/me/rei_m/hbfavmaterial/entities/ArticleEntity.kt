package me.rei_m.hbfavmaterial.entities

import java.io.Serializable

/**
 * 記事情報のEntity.
 */
data class ArticleEntity(val title: String,
                         val url: String,
                         val bookmarkCount: Int,
                         val iconUrl: String,
                         val body: String,
                         val bodyImageUrl: String) : Serializable
