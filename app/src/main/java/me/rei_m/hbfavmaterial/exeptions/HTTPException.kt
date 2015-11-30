package me.rei_m.hbfavmaterial.exeptions

import java.net.ProtocolException

public class HTTPException : ProtocolException {

    var statusCode: Int? = null
        private set

    constructor(statusCode: Int) : super() {
        this.statusCode = statusCode
    }

    constructor(statusCode: Int, detailMessage: String?) : super(detailMessage) {
        this.statusCode = statusCode
    }
}