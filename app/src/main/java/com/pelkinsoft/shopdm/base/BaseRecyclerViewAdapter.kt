package com.pelkinsoft.shopdm.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/** This class helps with unsubscribing observables when the adapter is detached or replaced by another
 * It uses `diffCallback` which optimises the way android deals with showing the items list
 */

abstract class BaseRecyclerViewAdapter<ItemType>(diffCallback: DiffUtil.ItemCallback<ItemType>) : ListAdapter<ItemType, RecyclerView.ViewHolder>(diffCallback) {
    private val compositeDisposable = CompositeDisposable()

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable.clear()
    }

    protected fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

    abstract class BaseDiffUtilCallback<ItemType> : DiffUtil.ItemCallback<ItemType>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ItemType, newItem: ItemType): Boolean {
            return oldItem == newItem
        }
    }
}