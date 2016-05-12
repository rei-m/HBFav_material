package me.rei_m.hbfavmaterial.network.response

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rdf:RDF", strict = false)
class HatenaRssXml {
    @set:ElementList(inline = true)
    @get:ElementList(inline = true)
    var list: List<HatenaRssItemXml>? = null
}
