package me.rei_m.hbfavmaterial.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import me.rei_m.hbfavmaterial.databinding.FragmentBookmarkBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.BookmarkClickedEvent
import me.rei_m.hbfavmaterial.events.ui.BookmarkCountClickedEvent
import me.rei_m.hbfavmaterial.events.ui.BookmarkUserClickedEvent

class BookmarkFragment : Fragment(), IFragmentAnimation {

    private val mBookmarkEntity: BookmarkEntity by lazy {
        arguments.getSerializable(ARG_BOOKMARK) as BookmarkEntity
    }

    override var mContainerWidth: Float = 0.0f

    companion object {

        private val ARG_BOOKMARK = "ARG_BOOKMARK"

        fun newInstance(bookmarkEntity: BookmarkEntity): BookmarkFragment {
            return BookmarkFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BOOKMARK, bookmarkEntity)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        // 全体
        with(binding) {
            bookmarkEntity = mBookmarkEntity

            // ヘッダ部分
            fragmentBookmarkLayoutHeader.bookmarkEntity = mBookmarkEntity

            // ブックマーク部分
            layoutBookmarkContentsLayoutBookmark.bookmarkEntity = mBookmarkEntity

            // 記事部分
            layoutBookmarkContentsLayoutArticle.bookmarkEntity = mBookmarkEntity

            // コンテンツ部分を押した時のイベント
            layoutBookmarkContents.setOnClickListener {
                EventBusHolder.EVENT_BUS.post(BookmarkClickedEvent(mBookmarkEntity))
            }

            // ブックマークユーザー数を押した時のイベント
            fragmentBookmarkTextBookmarkCount.setOnClickListener {
                EventBusHolder.EVENT_BUS.post(BookmarkCountClickedEvent(mBookmarkEntity))
            }

            // ヘッダ部分を押した時のイベント
            fragmentBookmarkLayoutHeader.root.setOnClickListener {
                EventBusHolder.EVENT_BUS.post(BookmarkUserClickedEvent(mBookmarkEntity.creator))
            }
        }

        setContainerWidth(container!!)

        return binding.root
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }
}
