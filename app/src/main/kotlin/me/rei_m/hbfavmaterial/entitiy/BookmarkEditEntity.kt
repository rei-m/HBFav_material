package me.rei_m.hbfavmaterial.entitiy

import java.io.Serializable

data class BookmarkEditEntity(val url: String,
                              val comment: String,
                              val isPrivate: Boolean,
                              val tags: List<String>) : Serializable