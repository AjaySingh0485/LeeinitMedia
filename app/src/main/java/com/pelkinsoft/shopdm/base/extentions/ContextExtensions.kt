package com.pelkinsoft.shopdm.base.extentions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.inputmethod.InputMethodManager
import timber.log.Timber

fun Context?.hideKeyboard() {
    if (this == null) {
        return
    }
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    var activity: Activity? = null
    if (this is Activity) {
        activity = this
    } else if (this is ContextWrapper) {
        val parentContext = this.baseContext
        if (parentContext is Activity) {
            activity = parentContext
        }
    }

    if (activity == null) {
        Timber.w("Try to hide keyboard but context type is incorrect %s", this.javaClass.simpleName)
        return
    }

    if (activity.currentFocus != null) {
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    } else {
        Timber.w("Try to hide keyboard but there is no current focus view")
    }
}

fun Context?.showKeyboard() {
    if (this == null) {
        return
    }
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    var activity: Activity? = null
    if (this is Activity) {
        activity = this
    } else if (this is ContextWrapper) {
        val parentContext = this.baseContext
        if (parentContext is Activity) {
            activity = parentContext
        }
    }

    if (activity == null) {
        Timber.w("Try to show keyboard but context type is incorrect %s", this.javaClass.simpleName)
        return
    }

    val currentFocusView = activity.currentFocus
    currentFocusView?.postDelayed({ inputMethodManager.showSoftInput(currentFocusView, 0) }, 100)
            ?: Timber.w("Try to show keyboard but there is no current focus view")
}

fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5).toInt()
}