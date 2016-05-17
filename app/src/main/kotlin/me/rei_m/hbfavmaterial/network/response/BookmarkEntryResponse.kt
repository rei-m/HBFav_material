package me.rei_m.hbfavmaterial.network.response

data class BookmarkEntryResponse(val count: Int,
                                 val bookmarks: List<Entity>) {

    companion object {
        data class Entity(val timestamp: String,
                          val comment: String,
                          val user: String,
                          val tags: List<String>)
    }
}
