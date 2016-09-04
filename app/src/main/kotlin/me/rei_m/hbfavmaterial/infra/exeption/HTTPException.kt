package me.rei_m.hbfavmaterial.infra.exeption

import java.net.ProtocolException

class HTTPException : ProtocolException {

    var statusCode: Int
        private set

    constructor(statusCode: Int) : super() {
        this.statusCode = statusCode
    }
}
