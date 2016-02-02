package me.rei_m.hbfavmaterial.entities

import java.io.Serializable
import java.util.*

data class BookmarkEditEntity(val url: String,
                              val comment: String,
                              val isPrivate: Boolean,
                              val tags: ArrayList<String>) : Serializable