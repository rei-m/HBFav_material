package me.rei_m.hbfavmaterial.infra.network.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
class EntryRssItemXml {

    @set:Element
    @get:Element
    var title: String = ""

    @set:Element
    @get:Element
    var link: String = ""

    @set:Element(required = false)
    @get:Element(required = false)
    var description: String = ""

    @Path("dc/subject")
    @set:Element(required = false)
    @get:Element(required = false)
    var subject: String = ""

    @Path("dc/date")
    @set:Element(name = "date")
    @get:Element(name = "date")
    var dateString: String = ""

    @Path("hatena/bookmarkcount")
    @set:Element(name = "bookmarkcount")
    @get:Element(name = "bookmarkcount")
    var bookmarkCount: Int = 0

    @Path("content/encoded")
    @set:Element(name = "encoded")
    @get:Element(name = "encoded")
    var content: String = ""
}
