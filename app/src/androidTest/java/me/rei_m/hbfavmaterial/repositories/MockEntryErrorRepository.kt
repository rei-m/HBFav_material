package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryTypeFilter
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import rx.Observable
import java.net.HttpURLConnection

class MockEntryErrorRepository : EntryRepository() {

    override fun findByEntryTypeForHot(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(HTTPException(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }

    override fun findByEntryTypeForNew(entryTypeFilter: EntryTypeFilter): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(HTTPException(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }
}