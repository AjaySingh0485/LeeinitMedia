package com.pelkinsoft.shopdm.main

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Narola on 26/10/18.
 */

class NavigationAdapter(val adapterItems: MutableList<NavigationOptionModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemClickSubject: PublishSubject<NavigationOptionModel> = PublishSubject.create()
    var itemClick: Observable<NavigationOptionModel> = itemClickSubject.hide()


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {

        return NavigationViewHolder(NavigationView(parent.context).apply {
            itemClick.subscribe { option ->
                itemClickSubject.onNext(option)
            }
        })
    }

    override fun getItemCount(): Int {
        return adapterItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private class NavigationViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        adapterItems[position].selectedPosition = position
        (holder.itemView as NavigationView).bind(adapterItems[position])
    }


}