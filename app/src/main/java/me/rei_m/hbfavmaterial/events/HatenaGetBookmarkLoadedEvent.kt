package me.rei_m.hbfavmaterial.events

import me.rei_m.hbfavmaterial.entities.HatenaRestApiBookmarkResponse

public class HatenaGetBookmarkLoadedEvent(val response: HatenaRestApiBookmarkResponse?,
                                          val status: LoadedEventStatus)