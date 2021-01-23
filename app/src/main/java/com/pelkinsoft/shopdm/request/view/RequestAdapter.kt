package com.pelkinsoft.shopdm.request.view

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.pelkinsoft.shopdm.register.Request
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Narola on 26/10/18.
 */

class RequestAdapter(val adapterItems: MutableList<Request>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemClickSubject: PublishSubject<Request> = PublishSubject.create()
    var itemClick: Observable<Request> = itemClickSubject.hide()
//    private var isLastIndex: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {

        return ViewHolder(RequestView(parent.context).apply {
            itemClick.subscribe { option ->
                itemClickSubject.onNext(adapterItems[position])
            }
        })
    }

    override fun getItemCount(): Int {
        return adapterItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as RequestView).bind(adapterItems[position])
    }


}