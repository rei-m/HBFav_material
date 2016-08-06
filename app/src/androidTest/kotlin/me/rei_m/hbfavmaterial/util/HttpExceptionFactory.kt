package me.rei_m.hbfavmaterial.util

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import java.util.*

class HttpExceptionFactory {
    companion object {
        fun create(code: Int): HttpException =
                HttpException(Response.error<Objects>(code, ResponseBody.create(MediaType.parse("test"), "")))
    }
}