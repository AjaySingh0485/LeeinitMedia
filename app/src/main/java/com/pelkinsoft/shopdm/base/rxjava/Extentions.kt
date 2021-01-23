package com.pelkinsoft.shopdm.base.rxjava

import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.CompletableEmitter
import io.reactivex.MaybeEmitter
import io.reactivex.Observable
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit



fun Disposable.autoDispose(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun View.throttleClicks(): Observable<Unit> {
    return clicks().throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeAndObserveOnMainThread(onNext: (t: T) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext)
}

fun <T> Observable<T>.throttleClicks(): Observable<T> {
    return this.throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
}

fun <T> SingleEmitter<T>.onSafeSuccess(t: T) {
    if (!isDisposed) onSuccess(t)
}

fun <T> SingleEmitter<T>.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}

fun <T> MaybeEmitter<T>.onSafeSuccess(t: T) {
    if (!isDisposed) onSuccess(t)
}

fun <T> MaybeEmitter<T>.onSafeComplete() {
    if (!isDisposed) onComplete()
}

fun <T> MaybeEmitter<T>.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}

fun CompletableEmitter.onSafeComplete() {
    if (!isDisposed) onComplete()
}

fun CompletableEmitter.onSafeError(throwable: Throwable) {
    if (!isDisposed) onError(throwable)
}