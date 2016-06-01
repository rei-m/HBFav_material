package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.utils.HttpExceptionFactory
import rx.Observable
import java.net.HttpURLConnection

class MockEntryErrorRepository : EntryRepository() {

    override fun findByEntryTypeForHot(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }

    override fun findByEntryTypeForNew(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(HttpExceptionFactory.create(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }
}