package com.pelkinsoft.shopdm.request.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pelkinsoft.shopdm.CommonUtility
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.base.rxjava.autoDispose
import com.pelkinsoft.shopdm.base.rxjava.throttleClicks
import com.pelkinsoft.shopdm.register.Request
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.custom_chat_cell.view.*


/**
 * Created by Narola on 26/10/18.
 */
class RequestView(context: Context) : FrameLayout(context) {

    private val itemClickSubject: PublishSubject<Request> = PublishSubject.create()
    var itemClick: Observable<Request> = itemClickSubject.hide()

    private val compositeDisposable = CompositeDisposable()

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        View.inflate(context, R.layout.custom_chat_cell, this)
    }

    fun bind(request: Request) {

        var name:String=""
        var photo:String=""

        textName.text = request.message
        textDate.text = CommonUtility.getTimeAgo(request.created_date)



//        Glide.with(context)
//            .load(photo)
//            .apply(
//                RequestOptions()
//                    .placeholder(R.drawable.ico_user_placeholder)
//                    .override(100, 100)
//                    .circleCrop()
//            )
//            .into(imageUserProfile)
//


        throttleClicks().subscribe {
            itemClickSubject.onNext(request)
        }.autoDispose(compositeDisposable)


    }


}