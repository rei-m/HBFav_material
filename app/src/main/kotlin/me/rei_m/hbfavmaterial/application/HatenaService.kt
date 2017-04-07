package me.rei_m.hbfavmaterial.application

import io.reactivex.Observable
import me.rei_m.hbfavmaterial.model.entity.EditableBookmarkEntity

interface HatenaService {

    companion object {
        const val TAG_READ_AFTER = "あとで読む"
    }

    val completeFetchRequestTokenEvent: Observable<String>

    val completeRegisterAccessTokenEvent: Observable<Unit>

    val completeDeleteAccessTokenEvent: Observable<Unit>

    val confirmAuthorisedEvent: Observable<Boolean>
    
    val completeFindBookmarkByUrlEvent: Observable<EditableBookmarkEntity>

    val completeRegisterBookmarkEvent: Observable<Unit>

    val completeDeleteBookmarkEvent: Observable<Unit>

    val failAuthorizeHatenaEvent: Observable<Unit>

    val error: Observable<Unit>

    fun fetchRequestToken()

    fun registerAccessToken(requestToken: String)

    fun deleteAccessToken()

    fun confirmAuthorised()

    fun findBookmarkByUrl(urlString: String)

    fun registerBookmark(urlString: String,
                         comment: String,
                         isOpen: Boolean,
                         isReadAfter: Boolean,
                         tags: List<String> = listOf())

    fun deleteBookmark(urlString: String)
}
