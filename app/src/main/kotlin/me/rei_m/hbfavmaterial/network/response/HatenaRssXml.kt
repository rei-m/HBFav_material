package me.rei_m.hbfavmaterial.network.response

import org.simpleframework.xml.Root

@Root(name = "rdf:RDF")
class HatenaRssXml {
    private lateinit var list: List<HatenaRssItemXml>
}
