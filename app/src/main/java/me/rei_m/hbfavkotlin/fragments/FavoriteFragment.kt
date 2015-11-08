package me.rei_m.hbfavkotlin.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark
import me.rei_m.hbfavkotlin.network.BookmarkFavorite
import me.rei_m.hbfavkotlin.views.adapters.BookmarkAdaptor

import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

public class FavoriteFragment : Fragment() {

    private var mSubscription: Subscription? = null

    private var mAdaptor: BookmarkAdaptor? = null

    companion object {

        fun newInstance(): FavoriteFragment {
            return FavoriteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdaptor = BookmarkAdaptor(activity, R.layout.list_item_bookmark)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_favorite, container, false)

        val listView = view.findViewById(R.id.list_bookmark_favorite) as ListView

        val footerView = inflater.inflate(R.layout.list_fotter_loading, null, false)

        listView.addFooterView(footerView, null, false)

        listView.setOnScrollListener(object : AbsListView.OnScrollListener{

            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (0 < totalItemCount && totalItemCount == firstVisibleItem + visibleItemCount) {
                    if(!BookmarkFavorite.isLoading){
                        drawFavoriteList(mAdaptor!!.nextIndex)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

        listView.adapter = mAdaptor

        drawFavoriteList()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.unsubscribe()
    }

    private fun drawFavoriteList(startIndex: Int = 0){

        // Observerを作成
        val observer = object : Observer<Bookmark>{
            override fun onNext(t: Bookmark?) {
                mAdaptor?.add(t)
            }

            override fun onCompleted() {
                mAdaptor?.notifyDataSetChanged()
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

}
