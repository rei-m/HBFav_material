package me.rei_m.hbfavmaterial.events

import me.rei_m.hbfavmaterial.entities.HatenaGetBookmarkResponse

public class HatenaGetBookmarkLoadedEvent(val response: HatenaGetBookmarkResponse?,
                                          val status: LoadedEventStatus)