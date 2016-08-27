package me.rei_m.hbfavmaterial.testutil

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException

object TestUtil {
    fun createApiErrorResponse(statusCode: Int): HttpException {
        return HttpException(Response.error<HttpException>(statusCode, ResponseBody.create(MediaType.parse("application/json"), "")))
    }
}
