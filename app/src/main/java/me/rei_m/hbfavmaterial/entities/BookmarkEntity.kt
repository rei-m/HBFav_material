package me.rei_m.hbfavmaterial.entities

import java.io.Serializable
import java.util.*

public data class BookmarkEntity(val title: String,
                                 val link: String,
                                 val description: String,
                                 val creator: String,
                                 val date: Date,
                                 val bookmarkCount: Int,
                                 val bookmarkIconUrl: String,
                                 val articleIconUrl: String,
                                 val articleBody: String,
                                 val articleImageUrl: String,
                                 val tags: List<String> = ArrayList()) : Serializable