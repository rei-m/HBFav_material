package me.rei_m.hbfavmaterial.entities

import java.io.Serializable

public data class HatenaGetBookmarkResponse(val comment_raw: String,
                                            val private: Boolean,
                                            val eid: Int,
                                            val created_epoch: Int,
                                            val tags: List<String>,
                                            val permalink: String,
                                            val comment: String,
                                            val created_datetime: String,
                                            val user: String) : Serializable