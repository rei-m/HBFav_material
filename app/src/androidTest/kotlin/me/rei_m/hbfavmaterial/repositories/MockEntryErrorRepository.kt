package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import java.net.HttpURLConnection
import java.util.*

class MockEntryErrorRepository : EntryRepository() {

    companion object {
        private fun httpExceptionFactory(code: Int): HttpException =
                HttpException(Response.error<Objects>(code, ResponseBody.create(MediaType.parse("test"), "")))
    }

    override fun findByEntryTypeForHot(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(httpExceptionFactory(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }

    override fun findByEntryTypeForNew(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(httpExceptionFactory(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }
}