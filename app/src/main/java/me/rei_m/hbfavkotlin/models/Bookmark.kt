package me.rei_m.hbfavkotlin.models

import java.io.Serializable

public data class Bookmark(val title: String,
                           val link: String,
                           val description: String,
                           val creator: String,
                           val date: String,
                           val bookmarkCount: Int,
                           val bookmarkIconUrl: String,
                           val articleIconUrl: String,
                           val articleBody: String,
                           val articleImageUrl: String
                           ): Serializable