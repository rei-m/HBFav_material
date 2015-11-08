package me.rei_m.hbfavkotlin.models

public data class Bookmark(val title: String,
                           val link: String,
                           val description: String,
                           val creator: String,
                           val date: String,
                           val bookmarkCount: Int) {

    companion object {
        private val iconImageDomain = "http://cdn1.www.st-hatena.com/users/"
    }

    val iconUrl = "$iconImageDomain${creator.substring(0,2)}/$creator/profile.gif"
}