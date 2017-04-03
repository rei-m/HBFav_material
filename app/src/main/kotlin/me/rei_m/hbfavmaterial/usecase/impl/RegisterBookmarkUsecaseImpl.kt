package me.rei_m.hbfavmaterial.usecase.impl

import io.reactivex.Single
import me.rei_m.hbfavmaterial.domain.entity.BookmarkEditEntity
import me.rei_m.hbfavmaterial.domain.repository.HatenaTokenRepository
import me.rei_m.hbfavmaterial.domain.repository.TwitterSessionRepository
import me.rei_m.hbfavmaterial.domain.repository.UserRepository
import me.rei_m.hbfavmaterial.domain.service.HatenaService
import me.rei_m.hbfavmaterial.domain.service.TwitterService
import me.rei_m.hbfavmaterial.usecase.RegisterBookmarkUsecase

class RegisterBookmarkUsecaseImpl(private val userRepository: UserRepository,
                                  private val hatenaTokenRepository: HatenaTokenRepository,
                                  private val hatenaService: HatenaService,
                                  private val twitterService: TwitterService,
                                  private val twitterSessionRepository: TwitterSessionRepository) : RegisterBookmarkUsecase {

    companion object {

        private const val MAX_LENGTH_COMMENT_AT_TWITTER = 100

        private const val MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER = 10

        private const val MAX_LENGTH_TITLE_AT_TWITTER = MAX_LENGTH_COMMENT_AT_TWITTER + MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER
    }

    override fun register(url: String,
                          title: String,
                          comment: String,
                          tags: List<String>,
                          isOpen: Boolean,
                          isCheckedReadAfter: Boolean,
                          isShareAtTwitter: Boolean): Single<BookmarkEditEntity> {

        if (isShareAtTwitter) {
            twitterService.postTweet(createShareText(url, title, comment))
        }

        val oAuthTokenEntity = hatenaTokenRepository.resolve()

        val postTags = tags.toMutableList()
        if (isCheckedReadAfter) {
            if (!postTags.contains(HatenaService.TAG_READ_AFTER)) {
                postTags.add(HatenaService.TAG_READ_AFTER)
            }
        } else {
            if (postTags.contains(HatenaService.TAG_READ_AFTER)) {
                postTags.remove(HatenaService.TAG_READ_AFTER)
            }
        }
        
        return hatenaService.upsertBookmark(oAuthTokenEntity, url, comment, isOpen, postTags).doAfterSuccess {

            val userEntity = userRepository.resolve()
            userEntity.isCheckedPostBookmarkOpen = isOpen
            userEntity.isCheckedPostBookmarkReadAfter = isCheckedReadAfter
            userRepository.store(userEntity)

            val twitterSessionEntity = twitterSessionRepository.resolve()
            twitterSessionEntity.isShare = isShareAtTwitter
            twitterSessionRepository.store(twitterSessionEntity)
        }
    }

    private fun createShareText(url: String, title: String, comment: String): String {
        return if (comment.isNotEmpty()) {
            val postComment: String
            val postTitle: String
            if (MAX_LENGTH_COMMENT_AT_TWITTER < comment.length) {
                postComment = comment.take(MAX_LENGTH_COMMENT_AT_TWITTER - 1) + "..."
                postTitle = if (MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER < (title.length)) {
                    title.take(MAX_LENGTH_TITLE_WITH_COMMENT_AT_TWITTER - 1) + "..."
                } else {
                    title
                }
            } else {
                postComment = comment
                val postTitleLength = MAX_LENGTH_TITLE_AT_TWITTER - comment.length
                postTitle = if (postTitleLength < title.length) {
                    title.take(postTitleLength - 1) + "..."
                } else {
                    title
                }
            }
            "$postComment \"$postTitle\" $url"
        } else {
            val postTitle = if (MAX_LENGTH_TITLE_AT_TWITTER < title.length) {
                title.substring(0, MAX_LENGTH_TITLE_AT_TWITTER - 1) + "..."
            } else {
                title
            }
            "\"$postTitle\" $url"
        }
    }
}
