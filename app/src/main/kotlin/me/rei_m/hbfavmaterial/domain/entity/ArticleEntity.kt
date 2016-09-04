package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable

data class ArticleEntity(val title: String,
                         val url: String,
                         val bookmarkCount: Int,
                         val iconUrl: String,
                         val body: String,
                         val bodyImageUrl: String) : Serializable
