package me.rei_m.hbfavmaterial.network.response

class BookmarkEntryResponse {

    companion object {
        class Entity() {
            @JvmField var timestamp: String = ""

            @JvmField var comment: String = ""

            @JvmField var user: String = ""

            @JvmField var tags: List<String> = arrayListOf()
        }
    }

    var count: Int = 0
        get
        set

    var bookmarks: List<Entity> = arrayListOf()
}
