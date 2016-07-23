package me.rei_m.hbfavmaterial.network.response

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rdf:RDF", strict = false)
class BookmarkRssXml {
    @set:ElementList(inline = true, required = false)
    @get:ElementList(inline = true, required = false)
    var list: MutableList<BookmarkRssItemXml> = arrayListOf()
}
