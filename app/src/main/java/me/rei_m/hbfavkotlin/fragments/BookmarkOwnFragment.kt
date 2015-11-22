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
import me.rei_m.hbfavkotlin.events.BookmarkListClickEvent
import me.rei_m.hbfavkotlin.events.BookmarkOwnLoadedEvent
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.BookmarkOwnModel
import me.rei_m.hbfavkotlin.views.adapters.BookmarkAdapter
import me.rei_m.hbfavkotlin.events.BookmarkOwnLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class BookmarkOwnFragment : Fragment(), FragmentAnimationI {

    private var mAdapter: BookmarkAdapter? = null

    override var mContainerWidth: Float = 0.0f

    companion object {
        fun newInstance(): BookmarkOwnFragment {
            return BookmarkOwnFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = BookmarkAdapter(activity, R.layout.list_item_bookmark)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_bookmark_list, container, false)

        val listView = view.findViewById(R.id.list_bookmark) as ListView

        val footerView = View.inflate(context, R.layout.list_fotter_loading, null)

        listView.addFooterView(footerView, null, false)

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val favoriteModel = ModelLocator.get(ModelTag.OWN) as BookmarkOwnModel
                    if (!favoriteModel.isBusy) {
                        favoriteModel.fetch(mAdapter!!.nextIndex)
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

        listView.adapter = mAdapter

        setContainer(container!!)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        // EventBus登録
        EventBusHolder.EVENT_BUS.register(this);

        val bookmarkFavoriteModel = ModelLocator.get(ModelTag.OWN) as BookmarkOwnModel

        val displayedCount = mAdapter?.count!!

        if (displayedCount != bookmarkFavoriteModel.bookmarkList.size) {
            // 表示済の件数とModel内で保持している件数をチェックし、
            // 差分があれば未表示のブックマークがあるのでリストに表示する
            mAdapter?.clear()
            mAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
            mAdapter?.notifyDataSetChanged()
        } else if (displayedCount === 0) {
            // 1件も表示していなければお気に入りのブックマーク情報を取得する
            bookmarkFavoriteModel.fetch()
        }
    }

    override fun onPause() {
        super.onPause()

        // EventBus登録解除
        EventBusHolder.EVENT_BUS.unregister(this);
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    @Subscribe
    @SuppressWarnings("unused")
    public fun onBookmarkOwnLoaded(event: BookmarkOwnLoadedEvent) {
        when (event.type) {
            BookmarkOwnLoadedEvent.Companion.Type.COMPLETE -> {
                val bookmarkFavoriteModel = ModelLocator.get(ModelTag.OWN) as BookmarkOwnModel
                mAdapter?.clear()
                mAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
                mAdapter?.notifyDataSetChanged()
            }
            BookmarkOwnLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }
    }
}
