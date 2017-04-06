package me.rei_m.hbfavmaterial.domain.entity

import java.util.*

data class BookmarkUserEntity(val creator: String,
                              val iconUrl: String,
                              val comment: String,
                              val createdAt: Date)
