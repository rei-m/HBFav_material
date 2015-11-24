package me.rei_m.hbfavkotlin.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.entities.BookmarkEntity
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent
import me.rei_m.hbfavkotlin.events.BookmarkListClickEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.BookmarkFavoriteModel
import me.rei_m.hbfavkotlin.views.adapters.BookmarkListAdapter
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkFavoriteFragment : Fragment(), FragmentAnimationI {

    private var mListAdapter: BookmarkListAdapter? = null

    override var mContainerWidth: Float = 0.0f

    companion object {
        fun newInstance(): BookmarkFavoriteFragment {
            return BookmarkFavoriteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = BookmarkListAdapter(activity, R.layout.list_item_bookmark)
    }

    override fun onDestroy() {
        super.onDestroy()
        mListAdapter = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_bookmark_list, container, false)

        val listView = view.findViewById(R.id.list_bookmark) as ListView

        val footerView = View.inflate(context, R.layout.list_fotter_loading, null)

        listView.addFooterView(footerView, null, false)

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val favoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                    if (!favoriteModel.isBusy) {
                        favoriteModel.fetch(mListAdapter!!.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bookmarkEntity = parent?.adapter?.getItem(position) as BookmarkEntity
            EventBusHolder.EVENT_BUS.post(BookmarkListClickEvent(bookmarkEntity))
        }

        listView.adapter = mListAdapter

        setContainer(container!!)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this)

        val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel

        val displayedCount = mListAdapter?.count!!

        if (displayedCount != bookmarkFavoriteModel.bookmarkList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            mListAdapter?.clear()
            mListAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
            mListAdapter?.notifyDataSetChanged()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのブックマーク情報を取得する
            bookmarkFavoriteModel.fetch()
        }
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkFavoriteLoaded(event: BookmarkFavoriteLoadedEvent) {
        when (event.type) {
            BookmarkFavoriteLoadedEvent.Companion.Type.COMPLETE -> {
                val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                mListAdapter?.clear()
                mListAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
                mListAdapter?.notifyDataSetChanged()
            }
            BookmarkFavoriteLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }
    }
}
