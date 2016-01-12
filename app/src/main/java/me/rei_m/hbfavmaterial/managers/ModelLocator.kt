package me.rei_m.hbfavmaterial.managers

import java.util.*

class ModelLocator private constructor() {

    companion object {
        private val showcase = HashMap<Tag, Any>();

        enum class Tag {
            FAVORITE,
            OWN_BOOKMARK,
            HOT_ENTRY,
            NEW_ENTRY,
            USER,
            OTHERS_BOOKMARK,
            USER_REGISTER_BOOKMARK,
            HATENA,
            TWITTER,
        }

        fun register(tag: Tag, model: Any): Unit {
            showcase.put(tag, model)
        }

        fun get(tag: Tag): Any = showcase[tag]!!
    }
}
