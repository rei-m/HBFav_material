package me.rei_m.hbfavmaterial.entities

import java.io.Serializable

data class BookmarkEditEntity(val url: String,
                              val comment: String,
                              val isPrivate: Boolean) : Serializable