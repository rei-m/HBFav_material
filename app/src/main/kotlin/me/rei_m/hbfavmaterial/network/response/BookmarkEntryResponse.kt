package me.rei_m.hbfavmaterial.network.response

class BookmarkEntryResponse {
    var count: Int = 0
        get
        set

    var bookmarks: List<Entity> = arrayListOf()
}

class Entity() {
    var timestamp: String = ""

    var comment: String = ""

    var user: String = ""

    var tags: List<String> = arrayListOf()
}
