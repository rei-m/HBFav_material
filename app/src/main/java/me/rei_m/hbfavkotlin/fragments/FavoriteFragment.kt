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

import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark
import me.rei_m.hbfavkotlin.network.BookmarkFavorite
import me.rei_m.hbfavkotlin.views.adapters.BookmarkAdapter

public class FavoriteFragment : Fragment(), FragmentAnimationI {

    private var mListener: OnFragmentInteractionListener? = null

    private var mSubscription: Subscription? = null

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)

        val listView = view.findViewById(R.id.list_bookmark_favorite) as ListView

        val footerView = View.inflate(context, R.layout.list_fotter_loading, null)

        listView.addFooterView(footerView, null, false)

        listView.setOnScrollListener(object : AbsListView.OnScrollListener{

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    if(!BookmarkFavorite.isLoading){
                        drawFavoriteList(mAdapter!!.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

        listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mListener?.onClickFavoriteItem(parent?.adapter?.getItem(position) as Bookmark)
            }
        }

        listView.adapter = mAdapter

        if(mAdapter?.count!! === 0){
            drawFavoriteList()
        }

        setContainer(container!!)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animator = createAnimatorMoveSlide(transit, enter, nextAnim, activity)
        return animator ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    private fun drawFavoriteList(startIndex: Int = 0){

        // Observerを作成
        val observer = object : Observer<Bookmark>{
            override fun onNext(t: Bookmark?) {
                mAdapter?.add(t)
            }

            override fun onCompleted() {
                mAdapter?.notifyDataSetChanged()
            }

            override fun onError(e: Throwable?) {
                println("Error!! ${e?.message}")
            }
        }

        // Observableを作成
        // 配信時は新しいスレッド
        // 監視者はメインスレッド
        val observable = BookmarkFavorite.request(startIndex)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

        // 購読を開始
        mSubscription = observable.subscribe(observer)
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

    interface OnFragmentInteractionListener {
        fun onClickFavoriteItem(bookmark: Bookmark)
    }
}
