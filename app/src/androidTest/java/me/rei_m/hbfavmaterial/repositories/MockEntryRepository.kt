package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.EntryEntity
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import rx.Observable
import java.util.*

class MockEntryRepository : EntryRepository() {

    override fun findByEntryTypeForHot(entryType: BookmarkUtil.Companion.EntryType): Observable<List<EntryEntity>> {
        return createTestEntitiesObservable()
    }

    override fun findByEntryTypeForNew(entryType: BookmarkUtil.Companion.EntryType): Observable<List<EntryEntity>> {
        return createTestEntitiesObservable()
    }

    private fun createTestEntitiesObservable(): Observable<List<EntryEntity>> {
        val entryEntities = ArrayList<EntryEntity>()

        for (i in 0..24) {
            entryEntities.add(createTestEntity(i))
        }
        return Observable.just(entryEntities)
    }

    private fun createTestEntity(index: Int): EntryEntity {
        val articleEntity = ArticleEntity(title = "EntryEntity_" + index,
                url = "",
                bookmarkCount = 0,
                iconUrl = "",
                body = "",
                bodyImageUrl = "")
        return EntryEntity(articleEntity = articleEntity,
                description = "",
                date = Date(),
                subject = "")
    }
}