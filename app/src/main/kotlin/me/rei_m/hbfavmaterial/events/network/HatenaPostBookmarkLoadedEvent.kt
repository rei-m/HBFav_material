package me.rei_m.hbfavmaterial.events.network

import me.rei_m.hbfavmaterial.entities.BookmarkEditEntity

class HatenaPostBookmarkLoadedEvent(val bookmark: BookmarkEditEntity?,
                                    val status: LoadedEventStatus)
