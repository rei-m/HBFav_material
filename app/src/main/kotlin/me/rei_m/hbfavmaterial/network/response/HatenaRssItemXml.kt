package me.rei_m.hbfavmaterial.network.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item")
class HatenaRssItemXml {

    @set:Element
    @get:Element
    private lateinit var title: String
}