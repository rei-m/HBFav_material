package me.rei_m.hbfavmaterial.repositories

import me.rei_m.hbfavmaterial.entities.ArticleEntity
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.enums.ReadAfterFilter
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import java.net.HttpURLConnection
import java.util.*

class MockBookmarkRepository : BookmarkRepository() {

    companion object {
        val TEST_ID_SUCCESS = "success"
        val TEST_ID_SUCCESS_2 = "success_2"
        val TEST_ID_NOT_FOUND = "not_found"
        val TEST_ID_EMPTY = "empty"
    }

    override fun findByUserIdForFavorite(userId: String, startIndex: Int): Observable<List<BookmarkEntity>> {
        return createTestEntitiesObservable(userId, startIndex)
    }

    override fun findByUserId(userId: String, readAfterFilter: ReadAfterFilter, startIndex: Int): Observable<List<BookmarkEntity>> {
        return createTestEntitiesObservable(userId, startIndex)
    }

    override fun findByArticleUrl(articleUrl: String): Observable<List<BookmarkEntity>> {

        return super.findByArticleUrl(articleUrl)
    }

    private fun createTestEntitiesObservable(userId: String, startIndex: Int): Observable<List<BookmarkEntity>> {
        val bookmarkEntities = ArrayList<BookmarkEntity>()

        return when (userId) {
            TEST_ID_SUCCESS -> {
                for (i in (startIndex * 25)..((startIndex + 1) * 25 - 1)) {
                    bookmarkEntities.add(createTestEntity(i))
                }
                Observable.just(bookmarkEntities)
            }
            TEST_ID_NOT_FOUND -> {
                Observable.create<List<BookmarkEntity>> { t ->
                    HttpException(Response.error<Objects>(HttpURLConnection.HTTP_NOT_FOUND, null)).let {
                        t.onError(it)
                    }
                }
            }
            TEST_ID_EMPTY -> {
                Observable.just(bookmarkEntities)
            }
            else -> {
                Observable.just(bookmarkEntities)
            }
        }
    }

    private fun createTestEntity(index: Int): BookmarkEntity {
        val articleEntity = ArticleEntity(title = "BookmarkEntity_" + index,
                url = "",
                bookmarkCount = 0,
                iconUrl = "",
                body = "",
                bodyImageUrl = "")
        return BookmarkEntity(articleEntity = articleEntity,
                description = "",
                creator = "",
                date = Date(),
                bookmarkIconUrl = "",
                tags = ArrayList<String>())
    }
}