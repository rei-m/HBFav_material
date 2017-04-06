package me.rei_m.hbfavmaterial.domain.entity

import java.io.Serializable

data class EditableBookmarkEntity(val url: String,
                                  val isFirstEdit: Boolean,
                                  val comment: String = "",
                                  val isPrivate: Boolean = false,
                                  val tags: List<String> = listOf()) : Serializable
