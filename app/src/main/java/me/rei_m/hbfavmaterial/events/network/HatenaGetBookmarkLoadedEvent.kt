package me.rei_m.hbfavmaterial.events.network

import me.rei_m.hbfavmaterial.entities.HatenaRestApiBookmarkResponse
import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus

public class HatenaGetBookmarkLoadedEvent(val response: HatenaRestApiBookmarkResponse?,
                                          val status: LoadedEventStatus)
