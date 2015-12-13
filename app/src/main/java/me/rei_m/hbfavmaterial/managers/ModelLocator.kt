package me.rei_m.hbfavmaterial.managers

import java.util.*

public class ModelLocator private constructor() {

    companion object {
        private val showcase = HashMap<Tag, Any>();

        public enum class Tag {
            FAVORITE,
            OWN_BOOKMARK,
            HOT_ENTRY,
            NEW_ENTRY,
            USER,
            OTHERS_BOOKMARK,
            USER_REGISTER_BOOKMARK,
            HATENA,
        }

        public fun register(tag: Tag, model: Any): Unit {
            showcase.put(tag, model)
        }

        public fun get(tag: Tag): Any = showcase[tag]!!
    }
}