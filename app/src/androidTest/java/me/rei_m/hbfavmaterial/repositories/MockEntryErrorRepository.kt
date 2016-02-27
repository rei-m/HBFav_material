package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.enums.EntryType
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import rx.Observable
import java.net.HttpURLConnection

class MockEntryErrorRepository : EntryRepository() {

    override fun findByEntryTypeForHot(entryType: EntryType): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(HTTPException(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }

    override fun findByEntryTypeForNew(entryType: EntryType): Observable<List<EntryEntity>> {
        return Observable.create<List<EntryEntity>> { t ->
            t.onError(HTTPException(HttpURLConnection.HTTP_INTERNAL_ERROR))
        }
    }
}