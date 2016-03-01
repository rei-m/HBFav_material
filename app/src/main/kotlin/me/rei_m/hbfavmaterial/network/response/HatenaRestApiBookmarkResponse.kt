package me.rei_m.hbfavmaterial.network.response

import java.io.Serializable
import java.util.*

/**
 * はてぶAPIのレスポンス.
 */
data class HatenaRestApiBookmarkResponse(val comment_raw: String,
                                         val private: Boolean,
                                         val eid: Int,
                                         val created_epoch: Int,
                                         val tags: ArrayList<String>,
                                         val permalink: String,
                                         val comment: String,
                                         val created_datetime: String,
                                         val user: String) : Serializable
