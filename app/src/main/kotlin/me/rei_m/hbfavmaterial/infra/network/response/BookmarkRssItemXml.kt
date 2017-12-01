/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.infra.network.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
class BookmarkRssItemXml {

    @set:Element
    @get:Element
    var title: String = ""

    @set:Element
    @get:Element
    var link: String = ""

    @set:Element(required = false)
    @get:Element(required = false)
    var description: String = ""

    @Path("dc/creator")
    @set:Element
    @get:Element
    var creator: String = ""

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
