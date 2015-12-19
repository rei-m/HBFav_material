package me.rei_m.hbfavmaterial.entities

import java.io.Serializable
import java.util.*

public data class EntryEntity(val articleEntity: ArticleEntity,
                              val description: String,
                              val date: Date,
                              val subject: String) : Serializable