package me.rei_m.hbfavmaterial.events.network

import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity

class HatenaGetBookmarkLoadedEvent(val bookmarkEditEntity: BookmarkEditEntity?,
                                   val status: LoadedEventStatus)
