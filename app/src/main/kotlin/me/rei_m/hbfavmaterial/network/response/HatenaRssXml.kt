package me.rei_m.hbfavmaterial.network.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rdf:RDF", strict = false)
class HatenaRssXml {

    @set:ElementList(name = "item", inline = true, required = true)
    private var list: List<HatenaRssItemXml>? = null

    @set:Element(name = "item", required = true)
    private var item: HatenaRssItemXml? = null

    fun getList(): List<HatenaRssItemXml> = list!!
}
