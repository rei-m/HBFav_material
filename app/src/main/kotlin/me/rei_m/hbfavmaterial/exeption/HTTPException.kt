package me.rei_m.hbfavmaterial.exeption

import java.net.ProtocolException

class HTTPException : ProtocolException {

    var statusCode: Int
        private set

    constructor(statusCode: Int) : super() {
        this.statusCode = statusCode
    }

    constructor(statusCode: Int, detailMessage: String?) : super(detailMessage) {
        this.statusCode = statusCode
    }
}
