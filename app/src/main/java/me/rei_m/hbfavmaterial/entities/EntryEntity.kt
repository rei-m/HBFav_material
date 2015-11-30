package me.rei_m.hbfavmaterial.entities

import java.io.Serializable
import java.util.*

public data class EntryEntity(val title: String,
                              val link: String,
                              val description: String,
                              val date: Date,
                              val bookmarkCount: Int,
                              val subject: String,
                              val articleIconUrl: String,
                              val articleBody: String,
                              val articleImageUrl: String) : Serializable