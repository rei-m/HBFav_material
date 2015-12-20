package me.rei_m.hbfavmaterial.events.network

import me.rei_m.hbfavmaterial.events.network.LoadedEventStatus

public class HatenaOAuthRequestTokenLoadedEvent(val status: LoadedEventStatus,
                                                val authUrl: String? = null)
