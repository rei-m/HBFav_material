package me.rei_m.hbfavmaterial.application

import io.reactivex.Observable
import me.rei_m.hbfavmaterial.model.entity.EditableBookmarkEntity

interface HatenaService {

    companion object {
        const val TAG_READ_AFTER = "あとで読む"
    }

    val editableBookmark: Observable<EditableBookmarkEntity>

    val registeredEvent: Observable<Unit>

    val deletedEvent: Observable<Unit>

    val unauthorizedEvent: Observable<Unit>

    val raisedErrorEvent: Observable<Unit>

    val completeFetchRequestTokenEvent: Observable<String>

    val completeRegisterAccessTokenEvent: Observable<Unit>

    val completeDeleteAccessTokenEvent: Observable<Unit>

    val confirmAuthorisedEvent: Observable<Boolean>

    val isLoading: Observable<Boolean>
    
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
