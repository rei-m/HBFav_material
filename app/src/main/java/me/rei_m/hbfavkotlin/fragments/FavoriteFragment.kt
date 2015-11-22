package me.rei_m.hbfavkotlin.fragments

import android.content.Context
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
import me.rei_m.hbfavkotlin.events.EventBusHolder
import me.rei_m.hbfavkotlin.managers.ModelLocator
import me.rei_m.hbfavkotlin.models.BookmarkFavoriteModel
import me.rei_m.hbfavkotlin.views.adapters.BookmarkAdapter
import me.rei_m.hbfavkotlin.events.BookmarkFavoriteLoadedEvent.Companion.Type as EventType
import me.rei_m.hbfavkotlin.managers.ModelLocator.Companion.Tag as ModelTag

public class FavoriteFragment : Fragment(), FragmentAnimationI {

    private var mListener: OnFragmentInteractionListener? = null

    private var mAdapter: BookmarkAdapter? = null

    override var mContainerWidth: Float = 0.0f

    companion object {
        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = BookmarkAdapter(activity, R.layout.list_item_favorite)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)

        val listView = view.findViewById(R.id.list_bookmark_favorite) as ListView

        val footerView = View.inflate(context, R.layout.list_fotter_loading, null)

        listView.addFooterView(footerView, null, false)

        listView.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    val favoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
                    if (!favoriteModel.isBusy) {
                        favoriteModel.fetch(mAdapter!!.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            mListener?.onClickFavoriteItem(parent?.adapter?.getItem(position) as BookmarkEntity)
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

        val bookmarkFavoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel

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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = activity as OnFragmentInteractionListener;
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
                mAdapter?.clear()
                mAdapter?.addAll(bookmarkFavoriteModel.bookmarkList)
                mAdapter?.notifyDataSetChanged()
            }
            BookmarkFavoriteLoadedEvent.Companion.Type.ERROR -> {
                // TODO エラー表示
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onClickFavoriteItem(bookmarkEntity: BookmarkEntity)
    }
}
