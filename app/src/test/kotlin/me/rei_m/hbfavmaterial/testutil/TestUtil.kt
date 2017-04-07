package me.rei_m.hbfavmaterial.testutil

import io.reactivex.Observable
import me.rei_m.hbfavmaterial.model.entity.ArticleEntity
import me.rei_m.hbfavmaterial.model.entity.BookmarkEntity
import me.rei_m.hbfavmaterial.model.entity.EntryEntity
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.util.*

object TestUtil {

    private val date: Date = Date()

    fun <T> createApiErrorResponse(statusCode: Int): Observable<T> {
        return Observable.error(HttpException(Response.error<HttpException>(statusCode, ResponseBody.create(MediaType.parse("application/json"), ""))))
    }

    fun createTestBookmarkEntity(no: Int, description: String = ""): BookmarkEntity {
        return BookmarkEntity(
                article = ArticleEntity(
                        title = "ArticleEntity_title_$no",
                        url = "ArticleEntity_url_$no",
                        bookmarkCount = no,
                        iconUrl = "ArticleEntity_iconUrl_$no",
                        body = "ArticleEntity_body_$no",
                        bodyImageUrl = "ArticleEntity_bodyImageUrl_$no"
                ),
                description = description,
                creator = "BookmarkEntity_creator_$no",
                date = date,
                bookmarkIconUrl = "BookmarkEntity_bookmarkIconUrl_$no")
    }

    fun createTestEntryEntity(no: Int): EntryEntity {
        return EntryEntity(
                article = ArticleEntity(
                        title = "ArticleEntity_title_$no",
                        url = "ArticleEntity_url_$no",
                        bookmarkCount = no,
                        iconUrl = "ArticleEntity_iconUrl_$no",
                        body = "ArticleEntity_body_$no",
                        bodyImageUrl = "ArticleEntity_bodyImageUrl_$no"
                ),
                description = "Description_$no",
                date = date,
                subject = "Subject_$no")
    }
}
